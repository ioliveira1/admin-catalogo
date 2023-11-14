package com.ioliveira.admin.catalogo.application.castmember.retrieve.get;

import com.ioliveira.admin.catalogo.application.Fixture;
import com.ioliveira.admin.catalogo.application.UseCaseTest;
import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetCastMemberByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetCastMemberByIdUseCase useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Override
    public List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    public void givenAValidId_whenCallsGetCastMember_shouldReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var member = CastMember.newMember(expectedName, expectedType);

        final var expectedId = member.getId();

        when(castMemberGateway.findById(any()))
                .thenReturn(Optional.of(member));

        final var output = useCase.execute(expectedId.getValue());

        assertNotNull(output);
        assertEquals(expectedId.getValue(), output.id());
        assertEquals(expectedName, output.name());
        assertEquals(expectedType, output.type());
        assertEquals(member.getCreatedAt(), output.createdAt());
        assertEquals(member.getUpdatedAt(), output.updatedAt());

        verify(castMemberGateway).findById(eq(expectedId));
    }

    @Test
    public void givenAInvalidId_whenCallsGetCastMemberAndDoesNotExists_shouldReturnNotFoundException() {
        final var expectedId = CastMemberID.from("123");

        final var expectedErrorMessage = "CastMember with ID 123 was not found";

        when(castMemberGateway.findById(any()))
                .thenReturn(Optional.empty());

        final var output = assertThrows(NotFoundException.class, () -> {
            useCase.execute(expectedId.getValue());
        });

        assertNotNull(output);
        assertEquals(expectedErrorMessage, output.getMessage());

        verify(castMemberGateway).findById(eq(expectedId));
    }

}
