package com.dentalclinic.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LocalValidator {
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public boolean emailValid(String email){
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }

    public List<String> validateInput(String email, String password){
        List<String> errors = new ArrayList<>();

        if(email == null || email.isEmpty()){
            errors.add("Email is required");
        } else if (!emailValid(email)) {
            errors.add("Invalid email format");
        }

        if(password == null || password.isEmpty()){
            errors.add("Password is required");
        }
        return errors;
    }


}
