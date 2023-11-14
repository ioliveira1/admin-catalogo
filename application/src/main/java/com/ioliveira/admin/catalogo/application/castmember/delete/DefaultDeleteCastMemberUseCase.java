package com.ioliveira.admin.catalogo.application.castmember.delete;

import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;

import java.util.Objects;

public class DefaultDeleteCastMemberUseCase extends DeleteCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultDeleteCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public void execute(final String id) {
        this.castMemberGateway.deleteById(CastMemberID.from(id));
    }
}
