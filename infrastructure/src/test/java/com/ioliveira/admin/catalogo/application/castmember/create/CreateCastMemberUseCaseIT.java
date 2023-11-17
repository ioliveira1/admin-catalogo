package com.ioliveira.admin.catalogo.application.castmember.create;

import com.ioliveira.admin.catalogo.Fixture;
import com.ioliveira.admin.catalogo.IntegrationTest;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@IntegrationTest
public class CreateCastMemberUseCaseIT {

    @Autowired
    private CreateCastMemberUseCase useCase;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Test
    public void givenAValidCommand_whenCallsCreateCastMember_shouldReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var command = CreateCastMemberCommand.with(expectedName, expectedType);

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        final var castMemberPersisted = castMemberRepository.findById(output.id()).get();

        assertNotNull(castMemberPersisted.getId());
        assertEquals(expectedName, castMemberPersisted.getName());
        assertEquals(expectedType, castMemberPersisted.getType());
        assertNotNull(castMemberPersisted.getCreatedAt());
        assertNotNull(castMemberPersisted.getUpdatedAt());

        verify(castMemberGateway).create(any());
    }
}
