package com.bank.bankapi.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void transferRequestAcceptsValidPayload() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(1L);
        request.setToAccountId(2L);
        request.setAmount(new BigDecimal("10.50"));

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void transferRequestRejectsInvalidIdsAndAmountScale() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountId(0L);
        request.setToAccountId(-2L);
        request.setAmount(new BigDecimal("10.555"));

        Set<String> invalidFields = invalidFields(request);

        assertTrue(invalidFields.contains("fromAccountId"));
        assertTrue(invalidFields.contains("toAccountId"));
        assertTrue(invalidFields.contains("amount"));
    }

    @Test
    void createAccountRequestAcceptsUppercaseThreeLetterCurrency() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setCurrency("USD");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void createAccountRequestRejectsLowercaseCurrency() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setCurrency("usd");

        Set<String> invalidFields = invalidFields(request);

        assertTrue(invalidFields.contains("currency"));
    }

    @Test
    void registerRequestRejectsInvalidPhoneNumber() {
        RegisterRequest request = validRegisterRequest();
        request.setPhoneNumber("012345");

        Set<String> invalidFields = invalidFields(request);

        assertTrue(invalidFields.contains("phoneNumber"));
    }

    @Test
    void loginRequestRejectsInvalidPhoneNumber() {
        LoginRequest request = new LoginRequest();
        request.setPhoneNumber("012345");
        request.setPassword("Password1!");

        Set<String> invalidFields = invalidFields(request);

        assertTrue(invalidFields.contains("phoneNumber"));
    }

    private static RegisterRequest validRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("+79990000000");
        request.setPassword("Password1!");
        request.setFullName("Test User");
        request.setPin("1234");
        return request;
    }

    private <T> Set<String> invalidFields(T request) {
        return validator.validate(request)
                .stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }
}
