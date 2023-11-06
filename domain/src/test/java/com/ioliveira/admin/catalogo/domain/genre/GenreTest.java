package com.ioliveira.admin.catalogo.domain.genre;

import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        final var exception =
                assertThrows(NotificationException.class, () -> Genre.newGenre(null, expectedIsActive));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenInvalidEmptyName_whenCallsNewGenre_shouldReceiveAnError() {
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;

        final var exception =
                assertThrows(NotificationException.class, () -> Genre.newGenre(" ", expectedIsActive));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenInvalidNameLengthLessThan3_whenCallsNewGenreAndValidate_thenShouldReceiveError() {
        final var expectedName = "fi ";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedErrorCount = 1;

        final var exception =
                assertThrows(NotificationException.class, () -> Genre.newGenre(expectedName, expectedIsActive));

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

        final var exception =
                assertThrows(NotificationException.class, () -> Genre.newGenre(expectedName, expectedIsActive));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAnActiveGenre_whenCallsDeactivate_shouldInactivateAGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = 0;

        final var genre = Genre.newGenre(expectedName, true);

        final Instant createdAt = genre.getCreatedAt();
        final Instant updatedAt = genre.getUpdatedAt();

        assertNotNull(genre);
        assertTrue(genre.isActive());
        assertNull(genre.getDeletedAt());

        final var actualGenre = genre.deactivate();

        assertEquals(genre.getId(), actualGenre.getId());
        assertEquals(genre.getName(), actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories().size());
        assertEquals(createdAt, actualGenre.getCreatedAt());
        assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
        assertNotNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAnInactiveGenre_whenCallsActivate_shouldActivateAGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = 0;

        final var genre = Genre.newGenre(expectedName, false);

        final Instant createdAt = genre.getCreatedAt();
        final Instant updatedAt = genre.getUpdatedAt();

        assertNotNull(genre);
        assertFalse(genre.isActive());
        assertNotNull(genre.getDeletedAt());

        final var actualGenre = genre.activate();

        assertEquals(genre.getId(), actualGenre.getId());
        assertEquals(genre.getName(), actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories().size());
        assertEquals(createdAt, actualGenre.getCreatedAt());
        assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenre_whenCallsUpdate_shouldReturnGenreUpdated() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"));

        final var genre = Genre.newGenre("acao", false);

        final Instant createdAt = genre.getCreatedAt();
        final Instant updatedAt = genre.getUpdatedAt();

        assertNotNull(genre);
        assertFalse(genre.isActive());
        assertNotNull(genre.getDeletedAt());

        final var actualGenre = genre.update(expectedName, expectedIsActive, expectedCategories);

        assertEquals(genre.getId(), actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(createdAt, actualGenre.getCreatedAt());
        assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
        assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenre_whenCallsUpdateWithInvalidEmptyName_shouldReceiveNotificationException() {
        final var expectedName = " ";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"));
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;

        final var genre = Genre.newGenre("acao", false);

        final var exception = assertThrows(
                NotificationException.class,
                () -> genre.update(expectedName, expectedIsActive, expectedCategories)
        );

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallsAddCategory_shouldAddCategories() {
        final var seriesID = CategoryID.from("123");
        final var moviesID = CategoryID.from("456");

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(moviesID);

        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        genre.update(expectedName, expectedIsActive, List.of(seriesID, moviesID));

        assertEquals(2, genre.getCategories().size());

        final var createdAt = genre.getCreatedAt();
        final var updatedAt = genre.getUpdatedAt();

        genre.removeCategory(seriesID);

        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertEquals(expectedCategories, genre.getCategories());
        assertEquals(createdAt, genre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isAfter(updatedAt));
        assertNull(genre.getDeletedAt());
    }

    @Test
    public void givenAValidGenreWithCategories_whenCallsRemoveCategory_shouldRemoveCategory() {
        final var seriesID = CategoryID.from("123");
        final var moviesID = CategoryID.from("456");

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(seriesID, moviesID);

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        assertEquals(0, genre.getCategories().size());

        final var createdAt = genre.getCreatedAt();
        final var updatedAt = genre.getUpdatedAt();

        genre.addCategory(seriesID);
        genre.addCategory(moviesID);

        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertEquals(expectedCategories, genre.getCategories());
        assertEquals(createdAt, genre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isAfter(updatedAt));
        assertNull(genre.getDeletedAt());
    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallsAddCategories_shouldAddCategories() {
        final var seriesID = CategoryID.from("123");
        final var moviesID = CategoryID.from("456");

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(seriesID, moviesID);

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        final var createdAt = genre.getCreatedAt();
        final var updatedAt = genre.getUpdatedAt();

        genre.addCategories(expectedCategories);

        assertEquals(2, genre.getCategories().size());

        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertEquals(expectedCategories, genre.getCategories());
        assertEquals(createdAt, genre.getCreatedAt());
        assertTrue(genre.getUpdatedAt().isAfter(updatedAt));
        assertNull(genre.getDeletedAt());
    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallsAddCategoriesWithEmptyList_shouldReceiveOk() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        final var createdAt = genre.getCreatedAt();
        final var updatedAt = genre.getUpdatedAt();

        genre.addCategories(expectedCategories);

        assertEquals(0, genre.getCategories().size());

        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertEquals(expectedCategories, genre.getCategories());
        assertEquals(createdAt, genre.getCreatedAt());
        assertFalse(genre.getUpdatedAt().isAfter(updatedAt));
        assertNull(genre.getDeletedAt());
    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallsAddCategoriesWithNullList_shouldReceiveOk() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        final var createdAt = genre.getCreatedAt();
        final var updatedAt = genre.getUpdatedAt();

        genre.addCategories(null);

        assertEquals(0, genre.getCategories().size());

        assertNotNull(genre.getId());
        assertEquals(expectedName, genre.getName());
        assertEquals(expectedIsActive, genre.isActive());
        assertEquals(expectedCategories, genre.getCategories());
        assertEquals(createdAt, genre.getCreatedAt());
        assertFalse(genre.getUpdatedAt().isAfter(updatedAt));
        assertNull(genre.getDeletedAt());
    }
}
