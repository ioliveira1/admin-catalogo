package com.ioliveira.admin.catalogo.application.castmember.create;

import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;

public record CreateCastMemberOutput(String id) {

    public static CreateCastMemberOutput from(final CastMember member) {
        return new CreateCastMemberOutput(member.getId().getValue());
    }

    public static CreateCastMemberOutput from(final CastMemberID expectedId) {
        return new CreateCastMemberOutput(expectedId.getValue());
    }
}
