package com.ioliveira.admin.catalogo.domain.castmember;

import com.ioliveira.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CastMemberTest {

    @Test
    public void givenAValidParams_whenCallsNewMember_thenInstantiateACastMember() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var castMember = CastMember.newMember(expectedName, expectedType);

        assertNotNull(castMember);
        assertNotNull(castMember.getId());
        assertEquals(expectedName, castMember.getName());
        assertEquals(expectedType, castMember.getType());
        assertNotNull(castMember.getCreatedAt());
        assertNotNull(castMember.getUpdatedAt());
    }

    @Test
    public void givenAInvalidNullName_whenCallsNewMember_shouldReceiveANotification() {
        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var exception = assertThrows(
                NotificationException.class,
                () -> CastMember.newMember(expectedName, expectedType)
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAInvalidEmptyName_whenCallsNewMember_shouldReceiveANotification() {
        final var expectedName = " ";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var exception = assertThrows(
                NotificationException.class,
                () -> CastMember.newMember(expectedName, expectedType)
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAInvalidNameWithLengthMoreThan255_whenCallsNewMember_shouldReceiveANotification() {
        final var expectedName = """
                Gostaria de enfatizar que o consenso sobre a necessidade de qualificação auxilia a preparação e a
                composição das posturas dos órgãos dirigentes com relação às suas atribuições.
                Do mesmo modo, a estrutura atual da organização apresenta tendências no sentido de aprovar a
                manutenção das novas proposições.
                """;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";

        final var exception = assertThrows(
                NotificationException.class,
                () -> CastMember.newMember(expectedName, expectedType)
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAInvalidNullType_whenCallsNewMember_shouldReceiveANotification() {
        final var expectedName = "Vin Diesel";
        final CastMemberType expectedType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var exception = assertThrows(
                NotificationException.class,
                () -> CastMember.newMember(expectedName, expectedType)
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAValidCastMember_whenCallUpdate_shouldReceiveUpdated() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var castMember =
                CastMember.newMember("vind", CastMemberType.DIRECTOR);

        assertNotNull(castMember);
        assertNotNull(castMember.getId());

        final var actualID = castMember.getId();
        final var actualCreatedAt = castMember.getCreatedAt();
        final var actualUpdatedAt = castMember.getUpdatedAt();

        castMember.update(expectedName, expectedType);

        assertEquals(actualID, castMember.getId());
        assertEquals(expectedName, castMember.getName());
        assertEquals(expectedType, castMember.getType());
        assertEquals(actualCreatedAt, castMember.getCreatedAt());
        assertTrue(actualUpdatedAt.isBefore(castMember.getUpdatedAt()));
    }

    @Test
    public void givenAValidCastMember_whenCallUpdateWithInvalidNullName_shouldReceiveNotification() {
        final String expectedName = null;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var castMember =
                CastMember.newMember("vind", CastMemberType.DIRECTOR);

        assertNotNull(castMember);
        assertNotNull(castMember.getId());

        final var exception = assertThrows(
                NotificationException.class,
                () -> castMember.update(expectedName, expectedType)
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAValidCastMember_whenCallUpdateWithInvalidEmptyName_shouldReceiveNotification() {
        final var expectedName = " ";
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        final var castMember =
                CastMember.newMember("vind", CastMemberType.DIRECTOR);

        assertNotNull(castMember);
        assertNotNull(castMember.getId());

        final var exception = assertThrows(
                NotificationException.class,
                () -> castMember.update(expectedName, expectedType)
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAValidCastMember_whenCallUpdateWithLengthMoreThan255_shouldReceiveNotification() {
        final var expectedName = """
                Gostaria de enfatizar que o consenso sobre a necessidade de qualificação auxilia a preparação e a
                composição das posturas dos órgãos dirigentes com relação às suas atribuições.
                Do mesmo modo, a estrutura atual da organização apresenta tendências no sentido de aprovar a
                manutenção das novas proposições.
                """;
        final var expectedType = CastMemberType.ACTOR;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";

        final var actualMember =
                CastMember.newMember("vind", CastMemberType.DIRECTOR);

        assertNotNull(actualMember);
        assertNotNull(actualMember.getId());

        final var exception = assertThrows(
                NotificationException.class,
                () -> actualMember.update(expectedName, expectedType)
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAValidCastMember_whenCallUpdateWithInvalidNullType_shouldReceiveNotification() {
        final var expectedName = "Vin Diesel";
        final CastMemberType expectedType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var castMember =
                CastMember.newMember("vind", CastMemberType.DIRECTOR);

        assertNotNull(castMember);
        assertNotNull(castMember.getId());

        final var exception = assertThrows(
                NotificationException.class,
                () -> castMember.update(expectedName, expectedType)
        );

        assertNotNull(exception);
        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }
}
