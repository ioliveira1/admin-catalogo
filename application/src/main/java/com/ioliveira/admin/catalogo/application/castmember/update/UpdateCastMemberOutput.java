package com.ioliveira.admin.catalogo.application.castmember.update;

import com.ioliveira.admin.catalogo.domain.castmember.CastMember;

public record UpdateCastMemberOutput(String id) {

    public static UpdateCastMemberOutput from(final CastMember member) {
        return new UpdateCastMemberOutput(member.getId().getValue());
    }

}
