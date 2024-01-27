package com.nextgendynamics.crm.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class InvalidObjectException extends RuntimeException {

    private final Set<String> errorMessages;

}
