package com.nextgendynamics.crm.validator;

import com.nextgendynamics.crm.exception.InvalidObjectException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ObjectValidator<T> {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public void validate (T objectToValidate) {
        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);
        if (!violations.isEmpty()) {
            String objectString = toString(objectToValidate);
            var errorMessages = new LinkedHashSet<String>();
            errorMessages.add("Validation failed for object: " + objectString);
            errorMessages.addAll(violations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet()));

            throw new InvalidObjectException(errorMessages);
        }
    }

    public static <T> String toString(T object) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        String fieldValues = Arrays.stream(fields)
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        return field.getName() + "='" + field.get(object) + "'";
                    } catch (IllegalAccessException e) {
                        e.printStackTrace(); // Handle this according to your needs
                        return "";
                    }
                })
                .collect(Collectors.joining(", "));

        return clazz.getSimpleName() + "{" + fieldValues + "}";
    }
}
