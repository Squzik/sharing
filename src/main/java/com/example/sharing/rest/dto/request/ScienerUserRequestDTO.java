package org.example.sharing.rest.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
public class ScienerUserRequestDTO {
    @NotEmpty(message = "Ошибка! Вы не указали логин")
    private String login;
    @NotEmpty(message = "Ошибка! Вы не указали пароль")
    private String password;
}
