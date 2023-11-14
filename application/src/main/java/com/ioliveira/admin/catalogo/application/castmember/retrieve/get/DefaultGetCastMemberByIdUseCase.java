package com.ioliveira.admin.catalogo.application.castmember.retrieve.get;

import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetCastMemberByIdUseCase extends GetCastMemberByIdUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultGetCastMemberByIdUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public CastMemberOutput execute(final String id) {
        final CastMemberID memberID = CastMemberID.from(id);

        return this.castMemberGateway
                .findById(memberID)
                .map(CastMemberOutput::from)
                .orElseThrow(notFound(memberID));
    }

    private static Supplier<NotFoundException> notFound(final CastMemberID id) {
        return () -> NotFoundException.with(CastMember.class, id);
    }
}
