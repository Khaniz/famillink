package com.famillink.famillink.settings;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordDomain.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordDomain passwordDomain = (PasswordDomain) target;
        if (!passwordDomain.getNewPassword().equals(passwordDomain.getConfirmNewPassword())) {
            errors.rejectValue("newPassword", "wrong.value", "패스워드를 정확히 입력해주세요!");
        }
    }
}
