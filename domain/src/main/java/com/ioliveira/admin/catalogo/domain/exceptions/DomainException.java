package com.ioliveira.admin.catalogo.domain.exceptions;

import com.ioliveira.admin.catalogo.domain.validation.Error;

import java.util.List;

public class DomainException extends NoStackTraceException {

    protected final List<Error> errors;

    protected DomainException(final String message, final List<Error> errors) {
        super(message);
        this.errors = errors;
    }

    public static DomainException with(final Error errors) {
        return new DomainException(errors.message(), List.of(errors));
    }

    public static DomainException with(final List<Error> errors) {
        return new DomainException("", errors);
    }

    public List<Error> getErrors() {
        return errors;
    }
}
