package org.example.sharing.controller;

import org.example.sharing.common.DatabaseContainer;
import org.example.sharing.common.MinioContainer;
import org.example.sharing.rest.handler.exception.NotFoundException;
import org.example.sharing.security.jwt.JwtRequest;
import org.example.sharing.store.entity.User;
import org.example.sharing.store.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@Disabled
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Container
    public static PostgreSQLContainer<DatabaseContainer> dbContainer = DatabaseContainer.getInstance();
    @Container
    public static GenericContainer<MinioContainer> minioContainer = MinioContainer.getInstance();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String SECURITY_LOGIN_URL = "/api/v1/auth/login";
    private static final String SECURITY_ACCESS_URL = "/api/v1/auth";

    @BeforeEach
    void before() {
        userRepository.save(createUser());
    }

    @AfterEach
    void after() {
        userRepository.deleteAllInBatch();
    }

    private User createUser() {
        return User.builder()
                .surname("test")
                .name("test")
                .patronymic("test")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .phone("89998877999")
                .mail("test@test.test")
                .password(passwordEncoder.encode("testPassword1"))
                .build();
    }

    @Test
    void successAuthorizationTest() throws Exception {
        User user = getUser();
        user.setIsMailConfirm(true);
        userRepository.saveAndFlush(user);
        JwtRequest jwtRequest = JwtRequest.builder()
                .login("test@test.test")
                .password("testPassword1")
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post(SECURITY_LOGIN_URL)
                .content(mapper.writeValueAsString(jwtRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void AccessToUserStoreWithNoAuthority() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(SECURITY_ACCESS_URL + "/user").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void AccessToAdminStoreWithNoAuthority() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(SECURITY_ACCESS_URL + "/admin").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    private User getUser(){
        return userRepository.findAll().stream().findFirst().orElseThrow(
                () -> new NotFoundException(NotFoundException.CLIENT_NOT_FOUND)
        );
    }
}
