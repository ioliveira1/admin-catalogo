package com.ioliveira.admin.catalogo.infrastructure.castmember;

import com.ioliveira.admin.catalogo.Fixture;
import com.ioliveira.admin.catalogo.MySQLGatewayTest;
import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberType;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void givenAValidCastMember_whenCallsUpdate_shouldRefreshIt() {
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;

        final var member = CastMember.newMember("vind", CastMemberType.DIRECTOR);
        final var expectedId = member.getId();

        final var currentMember = repository.saveAndFlush(CastMemberJpaEntity.from(member));

        assertEquals(1, repository.count());
        assertEquals("vind", currentMember.getName());
        assertEquals(CastMemberType.DIRECTOR, currentMember.getType());

        final var actualMember = gateway.update(
                CastMember.clone(member).update(expectedName, expectedType)
        );

        assertEquals(1, repository.count());

        assertEquals(expectedId, actualMember.getId());
        assertEquals(expectedName, actualMember.getName());
        assertEquals(expectedType, actualMember.getType());
        assertEquals(member.getCreatedAt(), actualMember.getCreatedAt());
        assertTrue(member.getUpdatedAt().isBefore(actualMember.getUpdatedAt()));

        final var persistedMember = repository.findById(expectedId.getValue()).get();

        assertEquals(expectedId.getValue(), persistedMember.getId());
        assertEquals(expectedName, persistedMember.getName());
        assertEquals(expectedType, persistedMember.getType());
        assertEquals(member.getCreatedAt(), persistedMember.getCreatedAt());
        assertTrue(member.getUpdatedAt().isBefore(persistedMember.getUpdatedAt()));
    }

}