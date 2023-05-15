package org.example.sharing.rest.handler;

import org.example.sharing.rest.dto.response.sciener.ScienerApiErrorResponseDTO;
import org.example.sharing.rest.handler.exception.BadRequestException;
import org.example.sharing.rest.handler.exception.NotFoundException;
import org.example.sharing.rest.handler.exception.UnAuthorisedException;
import org.springframework.lang.NonNull;

public class ScienerErrorHandler {
    public static void check(@NonNull ScienerApiErrorResponseDTO err) {
        if (err.getErrcode() != null) {
            switch (err.getErrcode()) {
               case 0:
                    break;
                case -3007:
                    throw new BadRequestException(BadRequestException.SCIENER_CODE_ALREADY_EXISTS);
                case -2012:
                    throw new NotFoundException(NotFoundException.SCIENER_NULL_GATEWAY);
                case -1007:
                case -1003:
                    throw new NotFoundException(NotFoundException.SCIENER_LOCK_NOT_FOUND);
                case -3:
                    throw new BadRequestException(BadRequestException.SCIENER_INVALID_VALUE);
                case 10003:
                case 10011:
                    throw new UnAuthorisedException(UnAuthorisedException.SCIENER_JWT_TOKEN);
                case 10007:
                case 30002:
                case 30005:
                    throw new UnAuthorisedException(UnAuthorisedException.INCORRECT_VALUES);
                default:
                    throw new BadRequestException(BadRequestException.SCIENER_UNHANDLED_ERROR);
            }
        }
    }
}
