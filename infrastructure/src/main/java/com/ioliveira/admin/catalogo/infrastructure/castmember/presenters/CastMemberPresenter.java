package com.ioliveira.admin.catalogo.infrastructure.castmember.presenters;

import com.ioliveira.admin.catalogo.application.castmember.retrieve.get.CastMemberOutput;
import com.ioliveira.admin.catalogo.infrastructure.castmember.models.CastMemberResponse;

public interface CastMemberPresenter {

    static CastMemberResponse present(final CastMemberOutput output) {
        return new CastMemberResponse(
                output.id(),
                output.name(),
                output.type().name(),
                output.createdAt().toString(),
                output.updatedAt().toString()
        );
    }

}
