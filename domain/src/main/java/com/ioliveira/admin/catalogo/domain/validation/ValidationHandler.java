package com.ioliveira.admin.catalogo.domain.validation;

import java.util.List;

public interface ValidationHandler {

    ValidationHandler append(Error error);

    ValidationHandler append(ValidationHandler handler);

    <T> T validate(Validation<T> validation);

    interface Validation<T> {
        T validate();
    }

    default boolean hasErrors() {
        return getErrors() != null && !getErrors().isEmpty();
    }

    List<Error> getErrors();

}
