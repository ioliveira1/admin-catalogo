package com.ioliveira.admin.catalogo.domain.exceptions;

import com.ioliveira.admin.catalogo.domain.AggregateRoot;
import com.ioliveira.admin.catalogo.domain.Identifier;
import com.ioliveira.admin.catalogo.domain.validation.Error;

import java.util.Collections;
import java.util.List;

public class NotFoundException extends DomainException {
    protected NotFoundException(final String message, final List<Error> errors) {
        super(message, errors);
    }

    public static NotFoundException with(
            final Class<? extends AggregateRoot<?>> aggregate,
            final Identifier identifier
    ) {
        final var error = "%s with ID %s was not found".formatted(aggregate.getSimpleName(), identifier.getValue());
        return new NotFoundException(error, Collections.emptyList());
    }
}
