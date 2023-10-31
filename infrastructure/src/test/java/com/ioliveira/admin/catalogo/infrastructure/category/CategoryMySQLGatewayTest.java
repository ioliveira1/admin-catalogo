package com.ioliveira.admin.catalogo.infrastructure.category;

import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.category.CategorySearchQuery;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.MySQLGatewayTest;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void givenAValidCategory_whenCallsUpdate_shouldReturnCategoryUpdated() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var category = Category.newCategory("Film", null, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final var updatedCategory =
                category.clone().update(expectedName, expectedDescription, expectedIsActive);

        final var persistedCategory = categoryMySQLGateway.update(updatedCategory);

        assertEquals(1, categoryRepository.count());

        assertEquals(category.getId(), persistedCategory.getId());
        assertEquals(expectedName, persistedCategory.getName());
        assertEquals(expectedDescription, persistedCategory.getDescription());
        assertEquals(expectedIsActive, persistedCategory.isActive());
        assertEquals(category.getCreatedAt(), persistedCategory.getCreatedAt());
        assertTrue(category.getUpdatedAt().isBefore(persistedCategory.getUpdatedAt()));
        assertEquals(category.getDeletedAt(), persistedCategory.getDeletedAt());
        assertNull(persistedCategory.getDeletedAt());

        final CategoryJpaEntity categoryJpaEntity = categoryRepository.findById(category.getId().getValue()).get();

        assertEquals(category.getId().getValue(), categoryJpaEntity.getId());
        assertEquals(expectedName, categoryJpaEntity.getName());
        assertEquals(expectedDescription, categoryJpaEntity.getDescription());
        assertEquals(expectedIsActive, categoryJpaEntity.isActive());
        assertEquals(category.getCreatedAt(), categoryJpaEntity.getCreatedAt());
        assertTrue(category.getUpdatedAt().isBefore(categoryJpaEntity.getUpdatedAt()));
        assertEquals(category.getDeletedAt(), categoryJpaEntity.getDeletedAt());
        assertNull(categoryJpaEntity.getDeletedAt());
    }

    @Test
    public void givenAPrePersistedCategory_whenCallsDelete_shouldDeleteCategory() {
        final Category category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        categoryMySQLGateway.deleteById(category.getId());

        assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAnInvalidCategoryId_whenCallsDelete_shouldDoNothing() {
        final Category category = Category.newCategory("Filmes", "A categoria mais assistida", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        categoryMySQLGateway.deleteById(CategoryID.from("invalid"));

        assertEquals(1, categoryRepository.count());
    }

    @Test
    public void givenAPrePersistedCategory_whenCallsFindById_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final Category category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, categoryRepository.count());

        final Category categoryFound = categoryMySQLGateway.findById(category.getId()).get();

        assertEquals(category.getId(), categoryFound.getId());
        assertEquals(expectedName, categoryFound.getName());
        assertEquals(expectedDescription, categoryFound.getDescription());
        assertEquals(expectedIsActive, categoryFound.isActive());
        assertEquals(category.getCreatedAt(), categoryFound.getCreatedAt());
        assertEquals(category.getUpdatedAt(), categoryFound.getUpdatedAt());
        assertEquals(category.getDeletedAt(), categoryFound.getDeletedAt());
        assertNull(categoryFound.getDeletedAt());
    }

    @Test
    public void givenPrePersistedCategories_whenCallsFindAll_shouldReturnCategoriesPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 5;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, categoryRepository.count());

        final var query = new CategorySearchQuery(0, 5, "", "name", "asc");
        final Pagination<Category> result = categoryMySQLGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedTotal, result.items().size());
        Assertions.assertEquals(documentarios.getId(), result.items().get(0).getId());
        Assertions.assertEquals(filmes.getId(), result.items().get(1).getId());
        Assertions.assertEquals(series.getId(), result.items().get(2).getId());
    }

    @Test
    public void givenEmptyDataBase_whenCallsFindAll_shouldReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        assertEquals(0, categoryRepository.count());

        final var query = new CategorySearchQuery(0, 1, "", "name", "asc");
        final Pagination<Category> result = categoryMySQLGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedPage, result.items().size());
    }

    public void givenPagination_whenCallsFindAllPage1_shouldReturnPaginated() {
        var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, categoryRepository.count());

        // Page 0
        var query = new CategorySearchQuery(0, 1, "", "name", "asc");
        Pagination<Category> result = categoryMySQLGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedPage, result.items().size());
        Assertions.assertEquals(documentarios.getId(), result.items().get(0).getId());

        // Page 1
        expectedPage = 1;
        query = new CategorySearchQuery(1, 1, "", "name", "asc");
        result = categoryMySQLGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedPage, result.items().size());
        Assertions.assertEquals(filmes.getId(), result.items().get(0).getId());

        // Page 2
        expectedPage = 2;
        query = new CategorySearchQuery(2, 1, "", "name", "asc");
        result = categoryMySQLGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedPage, result.items().size());
        Assertions.assertEquals(series.getId(), result.items().get(0).getId());
    }

    @Test
    public void givenPrePersistedCategories_whenCallsFindAllAndDocAsTerms_shouldReturnCategoriesPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", null, true);
        final var series = Category.newCategory("Séries", null, true);
        final var documentarios = Category.newCategory("Documentários", null, true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, categoryRepository.count());

        final var query = new CategorySearchQuery(0, 1, "doc", "name", "asc");
        final Pagination<Category> result = categoryMySQLGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedTotal, result.items().size());
        Assertions.assertEquals(documentarios.getId(), result.items().get(0).getId());
    }

    @Test
    public void givenPrePersistedCategories_whenCallsFindAllAndMaisAssistidaAsTerms_shouldReturnCategoriesPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final var series = Category.newCategory("Séries", "Uma categoria assistida", true);
        final var documentarios = Category.newCategory("Documentários", "A categoria menos assistida", true);

        assertEquals(0, categoryRepository.count());

        categoryRepository.saveAllAndFlush(List.of(
                CategoryJpaEntity.from(filmes),
                CategoryJpaEntity.from(series),
                CategoryJpaEntity.from(documentarios)
        ));

        assertEquals(3, categoryRepository.count());

        final var query = new CategorySearchQuery(0, 1, "MAIS ASSISTIDA", "name", "asc");
        final Pagination<Category> result = categoryMySQLGateway.findAll(query);

        Assertions.assertEquals(expectedPage, result.currentPage());
        Assertions.assertEquals(expectedPerPage, result.perPage());
        Assertions.assertEquals(expectedTotal, result.total());
        Assertions.assertEquals(expectedTotal, result.items().size());
        Assertions.assertEquals(filmes.getId(), result.items().get(0).getId());
    }
}
