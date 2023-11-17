package com.ioliveira.admin.catalogo.application.castmember.update;

import com.ioliveira.admin.catalogo.Fixture;
import com.ioliveira.admin.catalogo.IntegrationTest;
import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberType;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.admin.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class UpdateCastMemberUseCaseIT {

    @Autowired
    private UpdateCastMemberUseCase useCase;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @BeforeEach
    void cleanUp() {
        this.castMemberRepository.deleteAll();
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCastMember_shouldReturnItsIdentifier() {
        assertEquals(0, this.castMemberRepository.count());

        final var member = CastMember.newMember("vi diesel", CastMemberType.DIRECTOR);

        final var castMemberPersisted = this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        assertEquals(1, this.castMemberRepository.count());

        final var expectedId = castMemberPersisted.getId();
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var command = UpdateCastMemberCommand.with(
                expectedId,
                expectedName,
                expectedType
        );

        final var output = useCase.execute(command);

        final var castMemberUpdated = this.castMemberRepository.findById(output.id()).get();

        assertEquals(1, this.castMemberRepository.count());

        assertNotNull(output);
        assertEquals(expectedId, output.id());

        verify(castMemberGateway).findById(any());
        verify(castMemberGateway).update(any());

        assertEquals(expectedId, castMemberUpdated.getId());
        assertEquals(expectedName, castMemberUpdated.getName());
        assertEquals(expectedType, castMemberUpdated.getType());
        assertEquals(member.getCreatedAt(), castMemberUpdated.getCreatedAt());
        assertTrue(member.getUpdatedAt().isBefore(castMemberUpdated.getUpdatedAt()));
    }

    @Test
    public void givenAInvalidName_whenCallsUpdateCastMember_shouldThrowsNotificationException() {
        assertEquals(0, this.castMemberRepository.count());

        final var member = CastMember.newMember("vin diesel", CastMemberType.ACTOR);

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        assertEquals(1, this.castMemberRepository.count());

        final var expectedId = member.getId();
        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var command = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType
        );

        final var exception = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertEquals(1, this.castMemberRepository.count());

        assertNotNull(exception);

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(castMemberGateway).findById(any());
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    public void givenAInvalidType_whenCallsUpdateCastMember_shouldThrowsNotificationException() {
        assertEquals(0, this.castMemberRepository.count());

        final var member = CastMember.newMember("vin diesel", CastMemberType.ACTOR);

        this.castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        assertEquals(1, this.castMemberRepository.count());

        final var expectedId = member.getId();
        final var expectedName = Fixture.name();
        final CastMemberType expectedType = null;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var command = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType
        );

        final var exception = assertThrows(
                NotificationException.class,
                () -> useCase.execute(command)
        );

        assertEquals(1, this.castMemberRepository.count());

        assertNotNull(exception);

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(castMemberGateway).findById(any());
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    public void givenAInvalidId_whenCallsUpdateCastMember_shouldThrowsNotFoundException() {
        final var expectedId = CastMemberID.from("123");
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        final var command = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType
        );

        final var actualException = assertThrows(
                NotFoundException.class,
                () -> useCase.execute(command)
        );

        assertNotNull(actualException);

        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(castMemberGateway).findById(any());
        verify(castMemberGateway, times(0)).update(any());
    }
}
