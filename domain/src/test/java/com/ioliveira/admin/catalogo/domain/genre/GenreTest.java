package com.ioliveira.admin.catalogo.domain.genre;

import com.ioliveira.admin.catalogo.domain.exceptions.DomainException;
import com.ioliveira.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GenreTest {

    @Test
    public void givenValidParams_whenCallsNewGenre_shouldInstantiateAGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = 0;

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        assertNotNull(genre);
        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertEquals(expectedCategories, genre.getCategories().size());
        assertNotNull(genre.getCreatedAt());
        assertNotNull(genre.getUpdatedAt());
        assertNull(genre.getDeletedAt());
    }

    @Test
    public void givenInvalidNullName_whenCallsNewGenre_shouldReceiveAnError() {
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var genre = Genre.newGenre(null, expectedIsActive);

        final var exception =
                assertThrows(DomainException.class, () -> genre.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenInvalidEmptyName_whenCallsNewGenre_shouldReceiveAnError() {
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;

        final var genre = Genre.newGenre("  ", expectedIsActive);

        final var exception =
                assertThrows(DomainException.class, () -> genre.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenInvalidNameLengthLessThan3_whenCallsNewGenreAndValidate_thenShouldReceiveError() {
        final var expectedName = "fi ";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedErrorCount = 1;

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        final var exception = assertThrows(DomainException.class, () -> genre.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenInvalidNameLengthMoreThan255_whenCallsNewGenreAndValidate_thenShouldReceiveError() {
        final var expectedName = """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry.
                 Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer
                 took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries
                """;
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedErrorCount = 1;

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        final var exception = assertThrows(DomainException.class, () -> genre.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }
}
