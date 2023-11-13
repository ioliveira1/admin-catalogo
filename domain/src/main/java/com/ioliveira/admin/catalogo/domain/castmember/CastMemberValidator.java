package com.ioliveira.admin.catalogo.domain.castmember;

import com.ioliveira.admin.catalogo.domain.validation.ValidationHandler;
import com.ioliveira.admin.catalogo.domain.validation.Validator;

public class CastMemberValidator extends Validator {
    private final CastMember castMember;

    public CastMemberValidator(final ValidationHandler handler, final CastMember castMember) {
        super(handler);
        this.castMember = castMember;
    }

    @Override
    public void validate() {

    }
}
