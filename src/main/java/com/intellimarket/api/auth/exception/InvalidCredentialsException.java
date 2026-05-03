package com.intellimarket.api.auth.exception;

import com.intellimarket.api.shared.exception.BusinessRuleException;

public class InvalidCredentialsException extends BusinessRuleException {
    public InvalidCredentialsException() {
        super("Correo o contraseña incorrectos.");
    }
}