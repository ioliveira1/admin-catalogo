package com.ioliveira.admin.catalogo.application.category.retrieve.list;

import com.ioliveira.admin.catalogo.IntegrationTest;
import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class ListCategoriesUseCaseIT {

    @Autowired
    private ListCategoriesUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @BeforeEach
    void setup() {
        this.repository.deleteAll();

        final var categories = List.of(
                Category.newCategory("Filmes", null, true),
                Category.newCategory("Netflix Originals", "Títulos de autoria da Netflix", true),
                Category.newCategory("Amazon Originals", "Títulos de autoria da Amazon Prime", true),
                Category.newCategory("Documentários", null, true),
                Category.newCategory("Sports", null, true),
                Category.newCategory("Kids", "Categoria para crianças", true),
                Category.newCategory("Series", null, true)
        );

        this.repository.saveAllAndFlush(categories.stream().map(CategoryJpaEntity::from).toList());
    }

    @ParameterizedTest
    @CsvSource({
            "fil,0,10,1,1,Filmes",
            "net,0,10,1,1,Netflix Originals",
            "ZON,0,10,1,1,Amazon Originals",
            "KI,0,10,1,1,Kids",
            "crianças,0,10,1,1,Kids",
            "dA amAzO,0,10,1,1,Amazon Originals",
    })
    public void givenAValidTerm_whenCallsListCategoriesUseCase_shouldReturnCategoriesFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var result = useCase.execute(query);

        assertEquals(expectedItemsCount, result.items().size());
        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedTotal, result.total());
        assertEquals(expectedCategoryName, result.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,7,7,Amazon Originals",
            "name,desc,0,10,7,7,Sports",
            "createdAt,asc,0,10,7,7,Filmes",
            "createdAt,desc,0,10,7,7,Series"
    })
    public void givenAValidSortAndDirection_whenCallsListCategoriesUseCase_shouldReturnCategoriesOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName
    ) {
        final var expectedTerms = "";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var result = useCase.execute(query);

        assertEquals(expectedItemsCount, result.items().size());
        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedTotal, result.total());
        assertEquals(expectedCategoryName, result.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,7,Amazon Originals;Documentários",
            "1,2,2,7,Filmes;Kids",
            "2,2,2,7,Netflix Originals;Series",
            "3,2,1,7,Sports",
    })
    public void givenAValidPage_whenCallsListCategoriesUseCase_shouldReturnCategoriesPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoriesName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTerms = "";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var result = useCase.execute(query);

        assertEquals(expectedItemsCount, result.items().size());
        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedPage, result.currentPage());
        assertEquals(expectedTotal, result.total());

        int index = 0;
        for (String expectedName : expectedCategoriesName.split(";")) {
            assertEquals(expectedName, result.items().get(index).name());
            index++;
        }
    }
}
