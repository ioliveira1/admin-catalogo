package com.ioliveira.admin.catalogo.application.castmember.delete;

import com.ioliveira.admin.catalogo.Fixture;
import com.ioliveira.admin.catalogo.IntegrationTest;
import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class DeleteCastMemberUseCaseIT {

    @Autowired
    private DeleteCastMemberUseCase useCase;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @BeforeEach
    void cleanUp() {
        this.castMemberRepository.deleteAll();
    }

    @Test
    public void givenAValidId_whenCallsDeleteCastMember_shouldDeleteIt() {
        assertEquals(0, castMemberRepository.count());

        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());

        final var castMemberPersisted = this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        assertEquals(1, castMemberRepository.count());

        final var expectedId = castMemberPersisted.getId();

        assertDoesNotThrow(() -> useCase.execute(expectedId));

        assertEquals(0, castMemberRepository.count());

        verify(castMemberGateway).deleteById(any());
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteCastMember_shouldBeOk() {
        assertEquals(0, castMemberRepository.count());

        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        assertEquals(1, castMemberRepository.count());

        final var expectedId = CastMemberID.from("123");

        assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        assertEquals(1, castMemberRepository.count());

        verify(castMemberGateway).deleteById(any());
    }
}
