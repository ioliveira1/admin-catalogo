package com.ioliveira.admin.catalogo.application.genre.retrieve.get;

import com.ioliveira.admin.catalogo.IntegrationTest;
import com.ioliveira.admin.catalogo.application.genre.retreieve.get.GetGenreByIdUseCase;
import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.genre.Genre;
import com.ioliveira.admin.catalogo.domain.genre.GenreGateway;
import com.ioliveira.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class GetGenreByIdUseCaseIT {

    @Autowired
    private GetGenreByIdUseCase useCase;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        this.genreRepository.deleteAll();
    }

    @Test
    public void givenAValidId_whenCallsGetGenreById_shouldReturnGenre() {
        final var filmes =
                categoryGateway.create(Category.newCategory("Filmes", null, true));

        final var series =
                categoryGateway.create(Category.newCategory("Series", null, true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());

        assertEquals(0, genreRepository.count());

        final Genre genre =
                genreGateway.create(Genre.newGenre(expectedName, expectedIsActive).addCategories(expectedCategories));

        final var expectedId = genre.getId();

        assertEquals(1, genreRepository.count());

        final var actualGenre = useCase.execute(expectedId.getValue());

        assertEquals(expectedId.getValue(), actualGenre.id().getValue());
        assertEquals(expectedName, actualGenre.name());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(sorted(expectedCategories), sorted(actualGenre.categories().stream().map(CategoryID::from).toList()));
        assertEquals(expectedCategories.size(), actualGenre.categories().size());
        assertEquals(genre.getCreatedAt(), actualGenre.createdAt());
        assertEquals(genre.getUpdatedAt(), actualGenre.updatedAt());
        assertEquals(genre.getDeletedAt(), actualGenre.deletedAt());
    }

    private List<CategoryID> sorted(final List<CategoryID> categories) {
        return categories.stream()
                .sorted(Comparator.comparing(CategoryID::getValue))
                .toList();
    }
}
