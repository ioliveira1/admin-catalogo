package com.ioliveira.admin.catalogo.application.genre.create;

import com.ioliveira.admin.catalogo.IntegrationTest;
import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.ioliveira.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@IntegrationTest
public class CreateGenreUseCaseIT {

    @Autowired
    private CreateGenreUseCase useCase;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        this.genreRepository.deleteAll();
        this.categoryRepository.deleteAll();
    }

    @Test
    public void givenAValidCommand_whenCallsCreateGenre_shouldReturnGenreId() {
        final var filmes =
                categoryGateway.create(Category.newCategory("Filmes", null, true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId());

        final var command =
                CreateGenreCommand.with(expectedName, expectedIsActive, categoryIdsAsString(expectedCategories));

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        final var actualGenre = genreRepository.findById(output.id()).get();

        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(sorted(expectedCategories), sorted(actualGenre.getCategoryIDs()));
        assertEquals(expectedCategories.size(), actualGenre.getCategoryIDs().size());
        assertNotNull(actualGenre.getCreatedAt());
        assertNotNull(actualGenre.getUpdatedAt());
        assertNull(actualGenre.getDeletedAt());
    }

    private List<String> categoryIdsAsString(final List<CategoryID> expectedCategories) {
        return expectedCategories.stream().map(CategoryID::getValue).toList();
    }

    private List<CategoryID> sorted(final List<CategoryID> categories) {
        return categories.stream()
                .sorted(Comparator.comparing(CategoryID::getValue))
                .toList();
    }

}
