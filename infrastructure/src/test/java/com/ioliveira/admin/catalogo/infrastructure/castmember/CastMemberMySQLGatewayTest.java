package com.ioliveira.admin.catalogo.infrastructure.castmember;

import com.ioliveira.admin.catalogo.Fixture;
import com.ioliveira.admin.catalogo.MySQLGatewayTest;
import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MySQLGatewayTest
public class CastMemberMySQLGatewayTest {

    @Autowired
    private CastMemberMySQLGateway gateway;

    @Autowired
    private CastMemberRepository repository;

    @BeforeEach
    void cleanUp() {
        this.repository.deleteAll();
    }

    @Test
    public void givenAValidCastMember_whenCallsCreate_shouldPersistIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var member = CastMember.newMember(expectedName, expectedType);
        final var expectedId = member.getId();

        assertEquals(0, repository.count());

        final var actualMember = gateway.create(CastMember.clone(member));

        assertEquals(1, repository.count());

        assertEquals(expectedId, actualMember.getId());
        assertEquals(expectedName, actualMember.getName());
        assertEquals(expectedType, actualMember.getType());
        assertEquals(member.getCreatedAt(), actualMember.getCreatedAt());
        assertEquals(member.getUpdatedAt(), actualMember.getUpdatedAt());

        final var persistedMember = repository.findById(expectedId.getValue()).get();

        assertEquals(expectedId.getValue(), persistedMember.getId());
        assertEquals(expectedName, persistedMember.getName());
        assertEquals(expectedType, persistedMember.getType());
        assertEquals(member.getCreatedAt(), persistedMember.getCreatedAt());
        assertEquals(member.getUpdatedAt(), persistedMember.getUpdatedAt());
    }

}