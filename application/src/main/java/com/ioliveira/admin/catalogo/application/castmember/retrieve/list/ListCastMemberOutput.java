package com.ioliveira.admin.catalogo.application.castmember.retrieve.list;

import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberType;

import java.time.Instant;

public record ListCastMemberOutput(
        String id,
        String name,
        CastMemberType type,
        Instant createdAt
) {

    public static ListCastMemberOutput from(final CastMember castMember) {
        return new ListCastMemberOutput(
                castMember.getId().getValue(),
                castMember.getName(),
                castMember.getType(),
                castMember.getCreatedAt()
        );
    }

}
