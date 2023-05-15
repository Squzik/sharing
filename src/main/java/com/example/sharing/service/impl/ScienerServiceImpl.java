package org.example.sharing.service.impl;

import org.example.sharing.rest.dto.request.ScienerUserRequestDTO;
import org.example.sharing.rest.dto.response.sciener.ScienerApiCodeResponseDTO;
import org.example.sharing.rest.dto.response.sciener.ScienerApiErrorResponseDTO;
import org.example.sharing.rest.dto.response.sciener.ScienerApiLocksResponseDTO;
import org.example.sharing.rest.dto.response.sciener.ScienerApiUserResponseDTO;
import org.example.sharing.rest.dto.response.sciener.ScienerLocksResponseDto;
import org.example.sharing.rest.handler.ScienerErrorHandler;
import org.example.sharing.rest.handler.exception.BadRequestException;
import org.example.sharing.rest.handler.exception.NotFoundException;
import org.example.sharing.rest.handler.exception.UnAuthorisedException;
import org.example.sharing.rest.mapper.ScienerMapper;
import org.example.sharing.service.ScienerService;
import org.example.sharing.store.entity.ScienerCode;
import org.example.sharing.store.entity.ScienerLock;
import org.example.sharing.store.entity.ScienerUser;
import org.example.sharing.store.entity.User;
import org.example.sharing.store.repository.ScienerLockRepository;
import org.example.sharing.store.repository.ScienerUserRepository;
import org.example.sharing.store.repository.UserRepository;
import org.example.sharing.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.sharing.utils.ServiceUtils.genCode;
import static org.example.sharing.utils.ServiceUtils.getUserIdFromCurrentSession;

@Service
@Transactional
@RequiredArgsConstructor
public class ScienerServiceImpl implements ScienerService {

    private final RestTemplate rest = new RestTemplate();

    private final ScienerMapper scienerMapper;

    private final ScienerUserRepository scienerUserRepository;

    private final UserRepository userRepository;

    private final ScienerLockRepository lockRepository;

    @Value("${sciener.client_id}")
    private String clientId;

    @Value("${sciener.client_secret}")
    private String clientSecret;

    @Value("${sciener.auth}")
    private String auth;

    @Value("${sciener.create-code}")
    private String createCode;

    @Value("${sciener.create-custom-code}")
    private String createCustomCode;

    @Value("${sciener.delete-code}")
    private String deleteCode;

    @Value("${sciener.lock-detail}")
    private String lockDetail;

    @Value("${sciener.lock-list}")
    private String lockList;

    @Value("${sciener.open-lock}")
    private String openLock;

    /**
     * Авторизация аккаунта Sciener и его привязка к клиенту
     *
     * @param requestDTO - данные для авторизации
     * @return список замков аккаунта Sciener
     * @throws UnAuthorisedException - ошибки связанные с авторизацией
     */
    @Override
    public ScienerLocksResponseDto createUser(ScienerUserRequestDTO requestDTO) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("clientId", clientId);
        requestBody.add("clientSecret", clientSecret);
        requestBody.add("username", requestDTO.getLogin());
        requestBody.add("password", requestDTO.getPassword());

        ScienerApiUserResponseDTO response = rest.postForObject(
                auth,
                new HttpEntity<>(requestBody, getUrlencodedHeaders()),
                ScienerApiUserResponseDTO.class);

        ScienerErrorHandler.check(response);

        ScienerUser scienerUser = scienerMapper.userResponseToEntity(response);
        scienerUser.setUsername(requestDTO.getLogin().split("@")[0]);

        User user = userRepository.findById(getUserIdFromCurrentSession()).orElseThrow(
                () -> new NotFoundException(NotFoundException.CLIENT_NOT_FOUND)
        );
        user.setScienerUser(scienerUser);
        userRepository.saveAndFlush(user);

        return scienerMapper.toDto(user.getScienerUser(), pullAndSaveLocks(user.getScienerUser()));
    }

    /**
     * Отвязка и выход из аккаунта Sciener
     */
    @Override
    public void accountExit() {
        User user = userRepository.findById(getUserIdFromCurrentSession()).orElseThrow(
                () -> new NotFoundException(NotFoundException.CLIENT_NOT_FOUND)
        );
        if (Objects.isNull(user.getScienerUser()))
            throw new NotFoundException(NotFoundException.SCIENER_USER_NOT_FOUND);
        else {
            user.setScienerUser(null);
            userRepository.save(user);
        }
    }

    private Set<ScienerLock> pullAndSaveLocks(ScienerUser scienerUser) {
        Set<ScienerLock> locks = mergeLocksLists(
                getLockSetByClientFromApi(scienerUser.getId())
                        .stream()
                        .map(scienerMapper::lockToScienerLock)
                        .collect(Collectors.toSet()),
                lockRepository.getAllByScienerUserId(scienerUser.getId())
        );
        locks.forEach(lock -> {
            if (Objects.isNull(lock.getScienerUser())) {
                lock.setScienerUser(scienerUser);
            }
        });
        return Set.copyOf(lockRepository.saveAllAndFlush(locks));
    }

    @Override
    public ScienerLocksResponseDto getLocks() {
        User user = userRepository.findById(getUserIdFromCurrentSession()).orElseThrow(
                () -> new NotFoundException(NotFoundException.CLIENT_NOT_FOUND)
        );
        if (Objects.isNull(user.getScienerUser())) {
            throw new NotFoundException(NotFoundException.SCIENER_USER_NOT_FOUND);
        }
        return scienerMapper.toDto(user.getScienerUser(), pullAndSaveLocks(user.getScienerUser()));
    }

    @Override
    public ScienerLocksResponseDto getFreeLocks() {
        ScienerLocksResponseDto dto = getLocks();
        dto.setLocks(dto.getLocks()
                .stream()
                .filter(scienerLockResponseDTO -> scienerLockResponseDTO.getFlatId() == null && scienerLockResponseDTO.getIsLinked())
                .collect(Collectors.toList()));
        return dto;
    }

    @Override
    public void openLock(@NotNull ScienerLock lock) {
        if (BooleanUtils.isTrue(lock.getIsLinked())) {
            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("clientId", clientId);
            requestBody.add("accessToken", getAccessToken(lock.getScienerUser().getId()));
            requestBody.add("lockId", lock.getId());
            requestBody.add("date", System.currentTimeMillis());
            ScienerErrorHandler.check(rest.postForObject(
                    openLock,
                    new HttpEntity<>(requestBody, getUrlencodedHeaders()),
                    ScienerApiErrorResponseDTO.class
            ));
        } else {
            throw new NotFoundException(NotFoundException.SCIENER_LOCK_NOT_FOUND_IN_API);
        }
    }

    @Override
    public Boolean checkLockGatewayExists(ScienerLock lock) {
        return BooleanUtils.toBoolean(
                getLockFromApi(
                        lock.getScienerUser().getId(),
                        lock.getId()
                ).getHasGateway()
        );
    }


    @Override
    public Map<Integer, Boolean> checkLocksGatewayExists(Set<ScienerLock> scienerLocks) {
        Map<Integer, Boolean> gatewayExists = new HashMap<>();
        for (ScienerLock lock : scienerLocks) {
            gatewayExists.put(lock.getId(), checkLockGatewayExists(lock));
        }
        return gatewayExists;
    }

    /**
     * Метод получения списка замков из API
     *
     * @param scienerUserId - id Sciener аккаунта
     * @return - список замков в Response виде от API
     * @throws UnAuthorisedException - ошибки связанные с авторизацией
     */
    private Set<ScienerApiLocksResponseDTO.Lock> getLockSetByClientFromApi(Integer scienerUserId) {
        ScienerApiLocksResponseDTO responseDTO = rest.getForEntity(
                String.format(lockList,
                        clientId,
                        getAccessToken(scienerUserId),
                        System.currentTimeMillis()
                ),
                ScienerApiLocksResponseDTO.class).getBody();
        ScienerErrorHandler.check(responseDTO);
        return responseDTO.getList();
    }

    private ScienerApiLocksResponseDTO.Lock getLockFromApi(Integer scienerUserId, Integer scienerLockId) {
        ScienerApiLocksResponseDTO.Lock responseDTO = rest.getForEntity(
                String.format(lockDetail,
                        clientId,
                        getAccessToken(scienerUserId),
                        scienerLockId,
                        System.currentTimeMillis()
                ),
                ScienerApiLocksResponseDTO.Lock.class).getBody();
        ScienerErrorHandler.check(responseDTO);
        return responseDTO;
    }

    /**
     * Соединение уже существующего списка замков с новым из API
     * Так-же проводится пометка замков, которые пропали из API
     *
     * @param source - новые
     * @param target - старые
     * @return - совмещенный Set
     */
    private Set<ScienerLock> mergeLocksLists(Set<ScienerLock> source, Set<ScienerLock> target) {
        if (!target.isEmpty()) {
            Set<ScienerLock> dif = new HashSet<>(target);
            dif.removeAll(source);
            dif.forEach(lock -> lock.setIsLinked(false));
            dif.addAll(target);
            dif.addAll(source);
            return dif;
        }
        return source;
    }

    /**
     * Создание временного ключа к замку
     * При наличие у замка Gateway, будет создан 5'ти значный ключ, при отсутствии будет создан 9 значный ключ, настороне API
     *
     * @param lock  - замок
     * @param start - начало времени
     * @param end   - конец времени
     * @return Entity кода для дальнейшей работы с ним в сервисе аренды
     * @throws BadRequestException   - ошибка неправильных данных, особенно времени жизни ключа
     * @throws UnAuthorisedException - ошибки связанные с авторизацией
     */
    @Override
    public ScienerCode createCode(@NotNull ScienerLock lock, LocalDateTime start, LocalDateTime end) {
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("clientId", clientId);
        requestBody.add("accessToken", getAccessToken(lock.getScienerUser().getId()));
        requestBody.add("lockId", lock.getId());
        requestBody.add("keyboardPwdName", "flatSharing: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
        requestBody.add("startDate", start);
        requestBody.add("endDate", end);
        requestBody.add("date", System.currentTimeMillis());

        String apiUrl;
        Integer password = null;
        if (BooleanUtils.toBoolean(getLockFromApi(lock.getScienerUser().getId(), lock.getId()).getHasGateway())) {
            password = genCode(10000, 99999);
            requestBody.add("keyboardPwd", password);
            requestBody.add("addType", 2);
            apiUrl = createCustomCode;
        } else {
            requestBody.add("keyboardPwdType", 3);
            apiUrl = createCode;
        }

        ScienerApiCodeResponseDTO.Code response = rest.postForObject(
                apiUrl,
                new HttpEntity<>(requestBody, getUrlencodedHeaders()),
                ScienerApiCodeResponseDTO.Code.class);

        ScienerErrorHandler.check(response);

        ScienerCode code = scienerMapper.codeToScienerCode(response);
        code.setLock(lockRepository.getById(lock.getId()));
        if (password != null) {
            code.setPassword(password.toString());
        }
        return code;
    }


    /**
     * Удаление кода из замка и API
     *
     * @param code - код
     * @throws UnAuthorisedException - ошибки связанные с авторизацией
     */
    public void deleteCode(ScienerCode code) {
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("clientId", clientId);
        requestBody.add("accessToken", getAccessToken(code.getLock().getScienerUser().getId()));
        requestBody.add("lockId", code.getLock().getId());
        requestBody.add("keyboardPwdId", code.getId());
        requestBody.add("deleteType", 2);
        requestBody.add("date", System.currentTimeMillis());

        ScienerApiErrorResponseDTO response = rest.postForObject(
                deleteCode,
                new HttpEntity<>(requestBody, getUrlencodedHeaders()),
                ScienerApiErrorResponseDTO.class);

        ScienerErrorHandler.check(response);
    }

    /**
     * Удаление замка, который не существует на аккаунте пользователя в Sciener, из нашей бд
     *
     * @param lockId - id замка
     */
    @Override
    public void deleteLock(Integer lockId) {
        ScienerLock lock = lockRepository.findById(lockId).orElseThrow(
                () -> new NotFoundException(NotFoundException.SCIENER_LOCK_NOT_FOUND)
        );
        if (!lock.getScienerUser().getUser().getId().equals(ServiceUtils.getUserIdFromCurrentSession())) {
            throw new BadRequestException(BadRequestException.USER_MISMATCH);
        }
        if (BooleanUtils.isTrue(lock.getIsLinked())) {
            throw new BadRequestException(BadRequestException.SCIENER_LOCK_LINKED);
        }
        lock.getFlat().setScienerLock(null);
        lockRepository.delete(lock);
    }

    /**
     * Метод для получение токена доступа, необходимого для отправления запросов в API
     * Токены доступа и обновления, а так же время жизни токена доступа, сохраняются в базу данных для уменьшения
     * обращений к API
     * Когда токен доступа перестает быть активным, то происходит получение новых токенов и обновление их в БД
     *
     * @param userId - id Sciener акканута
     * @return токен доступа
     * @throws UnAuthorisedException - ошибки связанные с авторизацией
     */
    private String getAccessToken(Integer userId) {
        ScienerUser user = scienerUserRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(NotFoundException.SCIENER_USER_NOT_FOUND)
        );
        if (user.getTokenEndTime() <= System.currentTimeMillis()) {
            user.updateTokens(scienerMapper.userResponseToEntity(updateToken(user.getRefreshToken())));
            scienerUserRepository.saveAndFlush(user);
        }
        return user.getAccessToken();
    }

    /**
     * Метод получения новых токенов доступа и обновления
     *
     * @param refresh - токен обновления
     * @return токены доступа, обновления и время жизни токена доступа
     * @throws UnAuthorisedException - ошибки связанные с авторизацией
     */
    private ScienerApiUserResponseDTO updateToken(String refresh) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("clientId", clientId);
        requestBody.add("clientSecret", clientSecret);
        requestBody.add("grant_type", "refresh_token");
        requestBody.add("refresh_token", refresh);

        ScienerApiUserResponseDTO response = rest.postForObject(
                auth,
                new HttpEntity<>(requestBody, getUrlencodedHeaders()),
                ScienerApiUserResponseDTO.class
        );

        ScienerErrorHandler.check(response);

        return response;
    }

    /**
     * Метод для получения хедеров на urlencoded
     *
     * @return хедеры для urlencoded
     */
    private HttpHeaders getUrlencodedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        return headers;
    }
}
