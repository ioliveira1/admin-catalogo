package com.ioliveira.admin.catalogo.application.castmember.retrieve.get;

import com.ioliveira.admin.catalogo.Fixture;
import com.ioliveira.admin.catalogo.IntegrationTest;
import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class GetCastMemberByIdUseCaseIT {

    @Autowired
    private GetCastMemberByIdUseCase useCase;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Test
    public void givenAValidId_whenCallsGetCastMember_shouldReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var member = CastMember.newMember(expectedName, expectedType);

        final var castMemberPersisted = this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        Assertions.assertEquals(1, this.castMemberRepository.count());

        final var expectedId = castMemberPersisted.getId();

        final var output = useCase.execute(expectedId);

        assertNotNull(output);
        assertEquals(expectedId, output.id());
        assertEquals(expectedName, output.name());
        assertEquals(expectedType, output.type());
        assertEquals(member.getCreatedAt(), output.createdAt());
        assertEquals(member.getUpdatedAt(), output.updatedAt());

        verify(castMemberGateway).findById(any());
    }

    @Test
    public void givenAInvalidId_whenCallsGetCastMemberAndDoesNotExists_shouldReturnNotFoundException() {
        final var expectedId = CastMemberID.from("123");

        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        final var output = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(expectedId.getValue())
        );

        assertNotNull(output);
        assertEquals(expectedErrorMessage, output.getMessage());

        verify(castMemberGateway).findById(any());
    }

}
