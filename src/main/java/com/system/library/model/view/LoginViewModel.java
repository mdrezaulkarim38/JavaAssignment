package com.system.library.model.view;

import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginViewModel {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
