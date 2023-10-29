package com.ioliveira.admin.catalogo.infrastructure.category;

import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.infrastructure.MySQLGatewayTest;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@MySQLGatewayTest
public class CategoryMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryMySQLGateway;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void cleanUp() {
        this.categoryRepository.deleteAll();
    }

    @Test
    public void givenAValidCategory_whenCallsCreate_shouldReturnANewCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        final var persistedCategory = categoryMySQLGateway.create(category);

        assertEquals(1, categoryRepository.count());

        assertEquals(category.getId(), persistedCategory.getId());
        assertEquals(expectedName, persistedCategory.getName());
        assertEquals(expectedDescription, persistedCategory.getDescription());
        assertEquals(expectedIsActive, persistedCategory.isActive());
        assertEquals(category.getCreatedAt(), persistedCategory.getCreatedAt());
        assertEquals(category.getUpdatedAt(), persistedCategory.getUpdatedAt());
        assertEquals(category.getDeletedAt(), persistedCategory.getDeletedAt());
        assertNull(persistedCategory.getDeletedAt());

        final CategoryJpaEntity categoryJpaEntity = categoryRepository.findById(category.getId().getValue()).get();

        assertEquals(category.getId().getValue(), categoryJpaEntity.getId());
        assertEquals(expectedName, categoryJpaEntity.getName());
        assertEquals(expectedDescription, categoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, categoryJpaEntity.isActive());
        assertEquals(category.getCreatedAt(), categoryJpaEntity.getCreatedAt());
        assertEquals(category.getUpdatedAt(), categoryJpaEntity.getUpdatedAt());
        assertEquals(category.getDeletedAt(), categoryJpaEntity.getDeletedAt());
        assertNull(categoryJpaEntity.getDeletedAt());
    }
}
