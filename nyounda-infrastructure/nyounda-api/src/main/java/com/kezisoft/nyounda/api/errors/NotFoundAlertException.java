package com.kezisoft.nyounda.api.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;

import java.net.URI;

public class NotFoundAlertException extends ErrorResponseException {

    private static final long serialVersionUID = 1L;

    private final String entityName;

    private final String errorKey;

    public NotFoundAlertException(String defaultMessage, String entityName, String errorKey) {
        this(ErrorConstants.DEFAULT_TYPE, defaultMessage, entityName, errorKey);
    }

    public NotFoundAlertException(URI type, String defaultMessage, String entityName, String errorKey) {
        super(
                HttpStatus.NOT_FOUND,
                ProblemDetailWithCause.ProblemDetailWithCauseBuilder.instance()
                        .withStatus(HttpStatus.NOT_FOUND.value())
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

    public ProblemDetailWithCause getProblemDetailWithCause() {
        return (ProblemDetailWithCause) this.getBody();
    }
}
