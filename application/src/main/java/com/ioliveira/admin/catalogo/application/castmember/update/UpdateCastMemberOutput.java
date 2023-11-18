package com.ioliveira.admin.catalogo.application.castmember.update;

import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;

public record UpdateCastMemberOutput(String id) {

    public static UpdateCastMemberOutput from(final CastMember member) {
        return new UpdateCastMemberOutput(member.getId().getValue());
    }

    public static UpdateCastMemberOutput from(final CastMemberID castMemberID) {
        return new UpdateCastMemberOutput(castMemberID.getValue());
    }

}
