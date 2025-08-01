package com.kezisoft.nyounda.api.errors;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause.ProblemDetailWithCauseBuilder;

import java.net.URI;

@Getter
public class UnprocessableEntityAlertException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    private final String entityName;
    private final String errorKey;

    public UnprocessableEntityAlertException(String defaultMessage, String entityName, String errorKey) {
        this(ErrorConstants.DEFAULT_TYPE, defaultMessage, entityName, errorKey);
    }

    public UnprocessableEntityAlertException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ProblemDetailWithCauseBuilder.instance()
                        .withStatus(HttpStatus.UNPROCESSABLE_ENTITY.value())
                        .withType(type)
                        .withTitle(defaultMessage)
                        .withProperty("message", "error." + errorKey)
                        .withProperty("params", entityName)
                        .build(),
                null
        );
        this.entityName = entityName;
        this.errorKey = errorKey;
    }
}
