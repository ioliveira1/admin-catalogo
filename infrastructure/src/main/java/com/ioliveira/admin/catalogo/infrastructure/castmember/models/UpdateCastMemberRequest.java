package com.ioliveira.admin.catalogo.infrastructure.castmember.models;

import com.ioliveira.admin.catalogo.domain.castmember.CastMemberType;

public record UpdateCastMemberRequest(String name, CastMemberType type) {
}
