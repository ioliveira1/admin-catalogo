package com.ioliveira.admin.catalogo.application.castmember.create;

import com.ioliveira.admin.catalogo.application.Fixture;
import com.ioliveira.admin.catalogo.application.UseCaseTest;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateCastMemberUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateCastMemberUseCase useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Override
    public List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsCreateCastMember_shouldReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var command = CreateCastMemberCommand.with(expectedName, expectedType);

        when(castMemberGateway.create(any()))
                .thenAnswer(AdditionalAnswers.returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(castMemberGateway).create(argThat(member ->
                Objects.nonNull(member.getId())
                        && Objects.equals(expectedName, member.getName())
                        && Objects.equals(expectedType, member.getType())
                        && Objects.nonNull(member.getCreatedAt())
                        && Objects.nonNull(member.getUpdatedAt())
        ));
    }
}
