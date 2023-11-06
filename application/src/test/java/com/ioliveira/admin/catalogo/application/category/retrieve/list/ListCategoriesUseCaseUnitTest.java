package com.ioliveira.admin.catalogo.application.category.retrieve.list;

import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListCategoriesUseCaseUnitTest {

    @InjectMocks
    private DefaultListCategoriesUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        reset(categoryGateway);
    }

    @Test
    public void givenAValidQuery_whenCallsListCategoriesUseCase_shouldReturnCategories() {
        final List<Category> categories = List.of(
                Category.newCategory("Filmes", null, true),
                Category.newCategory("Filmes", null, true)
        );

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedPagination =
                new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

        final var expectedItemsCount = 2;
        final var expectedResult = expectedPagination.map(CategoryListOutput::from);

        when(categoryGateway.findAll(eq(query)))
                .thenReturn(expectedPagination);

        final var result = useCase.execute(query);

        assertEquals(expectedItemsCount, result.items().size());
        assertEquals(expectedResult, result);
        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPerPage, result.perPage());
        assertEquals(categories.size(), result.total());
    }

    @Test
    public void givenAValidQuery_whenHasNoResults_shouldReturnEmptyCategories() {
        final List<Category> categories = Collections.emptyList();

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedPagination =
                new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

        final var expectedItemsCount = 0;
        final var expectedResult = expectedPagination.map(CategoryListOutput::from);

        when(categoryGateway.findAll(eq(query)))
                .thenReturn(expectedPagination);

        final var result = useCase.execute(query);

        assertEquals(expectedItemsCount, result.items().size());
        assertEquals(expectedResult, result);
        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPerPage, result.perPage());
        assertEquals(categories.size(), result.total());
    }

    @Test
    public void givenAValidQuery_whenGatewayThrowsException_shouldReturnException() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedErrorMessage = "Gateway error";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        when(categoryGateway.findAll(eq(query)))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> useCase.execute(query));

        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
