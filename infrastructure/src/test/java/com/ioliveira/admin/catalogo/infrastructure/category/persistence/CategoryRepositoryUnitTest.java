package com.ioliveira.admin.catalogo.infrastructure.category.persistence;

import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.infrastructure.MySQLGatewayTest;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MySQLGatewayTest
public class CategoryRepositoryUnitTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAnInvalidNullName_whenCallsSave_shouldReturnError() {
        final var expectedPropertyName = "name";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.name";
        final Category category = Category.newCategory("Filme", "A categoria mais assistida", true);

        final CategoryJpaEntity entity = CategoryJpaEntity.from(category);
        entity.setName(null);

        final var exception =
                assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var cause = assertInstanceOf(PropertyValueException.class, exception.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedErrorMessage, cause.getMessage());
    }

    @Test
    public void givenAnInvalidNullCreatedAt_whenCallsSave_shouldReturnError() {
        final var expectedPropertyName = "createdAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.createdAt";
        final Category category = Category.newCategory("Filme", "A categoria mais assistida", true);

        final CategoryJpaEntity entity = CategoryJpaEntity.from(category);
        entity.setCreatedAt(null);

        final var exception =
                assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var cause = assertInstanceOf(PropertyValueException.class, exception.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedErrorMessage, cause.getMessage());
    }

    @Test
    public void givenAnInvalidNullUpdatedAt_whenCallsSave_shouldReturnError() {
        final var expectedPropertyName = "updatedAt";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.updatedAt";
        final Category category = Category.newCategory("Filme", "A categoria mais assistida", true);

        final CategoryJpaEntity entity = CategoryJpaEntity.from(category);
        entity.setUpdatedAt(null);

        final var exception =
                assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(entity));

        final var cause = assertInstanceOf(PropertyValueException.class, exception.getCause());

        assertEquals(expectedPropertyName, cause.getPropertyName());
        assertEquals(expectedErrorMessage, cause.getMessage());
    }
}
