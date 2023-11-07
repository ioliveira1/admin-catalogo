package com.ioliveira.admin.catalogo.application.genre.retrieve.get;

import com.ioliveira.admin.catalogo.application.UseCaseTest;
import com.ioliveira.admin.catalogo.application.genre.retreieve.get.DefaultGetGenreByIdUseCase;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.admin.catalogo.domain.genre.Genre;
import com.ioliveira.admin.catalogo.domain.genre.GenreGateway;
import com.ioliveira.admin.catalogo.domain.genre.GenreID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetGenreByIdUseCaseTest extends UseCaseTest {
    @InjectMocks
    private DefaultGetGenreByIdUseCase useCase;

    @Mock
    private GenreGateway genreGateway;

    @Override
    public List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Test
    public void givenAValidId_whenCallsGetGenre_shouldReturnGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(
                CategoryID.from("123"),
                CategoryID.from("456")
        );

        final var genre = Genre.newGenre(expectedName, expectedIsActive)
                .addCategories(expectedCategories);

        final var expectedId = genre.getId();

        when(genreGateway.findById(any()))
                .thenReturn(Optional.of(genre));

        final var actualGenre = useCase.execute(expectedId.getValue());

        assertEquals(expectedId.getValue(), actualGenre.id().getValue());
        assertEquals(expectedName, actualGenre.name());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(categoryIdsAsString(expectedCategories), actualGenre.categories());
        assertEquals(genre.getCreatedAt(), actualGenre.createdAt());
        assertEquals(genre.getUpdatedAt(), actualGenre.updatedAt());
        assertEquals(genre.getDeletedAt(), actualGenre.deletedAt());

        verify(genreGateway, times(1)).findById(eq(expectedId));
    }

    @Test
    public void givenAValidId_whenCallsGetGenreAndDoesNotExists_shouldReturnNotFound() {
        final var expectedErrorMessage = "Genre with ID 123 was not found";

        final var expectedId = GenreID.from("123");

        when(genreGateway.findById(eq(expectedId)))
                .thenReturn(Optional.empty());

        final var actualException = Assertions.assertThrows(NotFoundException.class, () -> {
            useCase.execute(expectedId.getValue());
        });

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    private List<String> categoryIdsAsString(final List<CategoryID> expectedCategories) {
        return expectedCategories.stream().map(CategoryID::getValue).toList();
    }
}
