package com.ioliveira.admin.catalogo.domain.category;

import com.ioliveira.admin.catalogo.domain.UnitTest;
import com.ioliveira.admin.catalogo.domain.exceptions.DomainException;
import com.ioliveira.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CategoryUnitTest extends UnitTest {

    @Test
    public void givenValidParams_whenCallsNewCategory_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));
        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals(expectedName, category.getName());
        assertEquals(expectedDescription, category.getDescription());
        assertEquals(expectedIsActive, category.isActive());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
        assertNull(category.getDeletedAt());
    }

    @Test
    public void givenInvalidNullName_whenCallsNewCategoryAndValidate_thenShouldReceiveError() {
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var category = Category.newCategory(null, expectedDescription, expectedIsActive);

        final var exception = assertThrows(DomainException.class, () -> category.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenInvalidEmptyName_whenCallsNewCategoryAndValidate_thenShouldReceiveError() {
        final var expectedName = "  ";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedErrorCount = 1;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var exception = assertThrows(DomainException.class, () -> category.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenInvalidNameLengthLessThan3_whenCallsNewCategoryAndValidate_thenShouldReceiveError() {
        final var expectedName = "fi ";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedErrorCount = 1;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var exception = assertThrows(DomainException.class, () -> category.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenInvalidNameLengthMoreThan255_whenCallsNewCategoryAndValidate_thenShouldReceiveError() {
        final var expectedName = """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry.
                 Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer
                 took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries
                """;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedErrorCount = 1;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        final var exception = assertThrows(DomainException.class, () -> category.validate(new ThrowsValidationHandler()));

        assertEquals(expectedErrorCount, exception.getErrors().size());
        assertEquals(expectedErrorMessage, exception.getErrors().get(0).message());
    }

    @Test
    public void givenValidEmptyDescription_whenCallsNewCategory_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "  ";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));
        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals(expectedName, category.getName());
        assertEquals(expectedDescription, category.getDescription());
        assertEquals(expectedIsActive, category.isActive());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
        assertNull(category.getDeletedAt());
    }

    @Test
    public void givenValidFalseIsActive_whenCallsNewCategory_thenInstantiateACategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));
        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals(expectedName, category.getName());
        assertEquals(expectedDescription, category.getDescription());
        assertEquals(expectedIsActive, category.isActive());
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
        assertNotNull(category.getDeletedAt());
    }

    @Test
    public void givenValidActiveCategory_whenCallsDeactivate_thenReturnCategoryInactivated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var category = Category.newCategory(expectedName, expectedDescription, true);

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        final Instant createdAt = category.getCreatedAt();
        final Instant updatedAt = category.getUpdatedAt();

        assertTrue(category.isActive());
        assertNull(category.getDeletedAt());

        final var actualCategory = category.deactivate();

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        assertEquals(category.getId(), actualCategory.getId());
        assertEquals(category.getName(), actualCategory.getName());
        assertEquals(category.getDescription(), actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(createdAt, actualCategory.getCreatedAt());
        assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt));
        assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenValidInactiveCategory_whenCallsActivate_thenReturnCategoryActivated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, false);

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        final Instant createdAt = category.getCreatedAt();
        final Instant updatedAt = category.getUpdatedAt();

        assertFalse(category.isActive());
        assertNotNull(category.getDeletedAt());

        final var actualCategory = category.activate();

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        assertEquals(category.getId(), actualCategory.getId());
        assertEquals(category.getName(), actualCategory.getName());
        assertEquals(category.getDescription(), actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(createdAt, actualCategory.getCreatedAt());
        assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt));
        assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenValidCategory_whenCallsUpdate_thenReturnCategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory("Film", "A categoria ", expectedIsActive);

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        final Instant createdAt = category.getCreatedAt();
        final Instant updatedAt = category.getUpdatedAt();

        final var actualCategory = category.update(expectedName, expectedDescription, expectedIsActive);

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        assertEquals(category.getId(), actualCategory.getId());
        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(createdAt, actualCategory.getCreatedAt());
        assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt));
        assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenValidCategory_whenCallsUpdateToDeactivate_thenReturnCategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var category = Category.newCategory("Film", "A categoria ", true);

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        assertTrue(category.isActive());
        assertNull(category.getDeletedAt());

        final Instant createdAt = category.getCreatedAt();
        final Instant updatedAt = category.getUpdatedAt();

        final var actualCategory = category.update(expectedName, expectedDescription, false);

        assertDoesNotThrow(() -> category.validate(new ThrowsValidationHandler()));

        assertEquals(category.getId(), actualCategory.getId());
        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(createdAt, actualCategory.getCreatedAt());
        assertTrue(actualCategory.getUpdatedAt().isAfter(updatedAt));
        assertNotNull(actualCategory.getDeletedAt());
    }
}
