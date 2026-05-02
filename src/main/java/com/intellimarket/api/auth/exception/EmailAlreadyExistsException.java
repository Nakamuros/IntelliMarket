package com.intellimarket.api.auth.exception;

import com.intellimarket.api.shared.exception.BusinessRuleException;

public class EmailAlreadyExistsException extends BusinessRuleException {
    public EmailAlreadyExistsException(String email) {
        super("El email " + email + " ya está registrado en el sistema.");
    }
}