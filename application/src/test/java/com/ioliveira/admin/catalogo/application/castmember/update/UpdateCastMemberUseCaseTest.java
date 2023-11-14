package com.ioliveira.admin.catalogo.application.castmember.update;

import com.ioliveira.admin.catalogo.application.Fixture;
import com.ioliveira.admin.catalogo.application.UseCaseTest;
import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberType;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateCastMemberUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateCastMemberUseCase useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Override
    public List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCastMember_shouldReturnItsIdentifier() {
        final var member = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);

        final var expectedId = member.getId();
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;

        final var command = UpdateCastMemberCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedType
        );

        when(castMemberGateway.findById(any()))
                .thenReturn(Optional.of(CastMember.clone(member)));

        when(castMemberGateway.update(any()))
                .thenAnswer(AdditionalAnswers.returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertEquals(expectedId.getValue(), output.id());

        verify(castMemberGateway).findById(eq(expectedId));

        verify(castMemberGateway).update(argThat(updatedMember ->
                Objects.equals(expectedId, updatedMember.getId())
                        && Objects.equals(expectedName, updatedMember.getName())
                        && Objects.equals(expectedType, updatedMember.getType())
                        && Objects.equals(member.getCreatedAt(), updatedMember.getCreatedAt())
                        && member.getUpdatedAt().isBefore(updatedMember.getUpdatedAt())
        ));
    }

    @Test
    public void givenAInvalidName_whenCallsUpdateCastMember_shouldThrowsNotificationException() {
        final var member = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);

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

        when(castMemberGateway.findById(any()))
                .thenReturn(Optional.of(member));

        final var exception = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(exception);

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(castMemberGateway).findById(eq(expectedId));
        verify(castMemberGateway, times(0)).update(any());
    }

    @Test
    public void givenAInvalidType_whenCallsUpdateCastMember_shouldThrowsNotificationException() {
        final var member = CastMember.newMember("vin diesel", CastMemberType.DIRECTOR);

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

        when(castMemberGateway.findById(any()))
                .thenReturn(Optional.of(member));

        final var exception = assertThrows(NotificationException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(exception);

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());

        verify(castMemberGateway).findById(eq(expectedId));
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

        when(castMemberGateway.findById(any()))
                .thenReturn(Optional.empty());

        final var actualException = assertThrows(NotFoundException.class, () -> {
            useCase.execute(command);
        });

        assertNotNull(actualException);

        assertEquals(expectedErrorMessage, actualException.getMessage());

        verify(castMemberGateway).findById(eq(expectedId));
        verify(castMemberGateway, times(0)).update(any());
    }
}
