package com.ioliveira.admin.catalogo.application.genre.delete;

import com.ioliveira.admin.catalogo.IntegrationTest;
import com.ioliveira.admin.catalogo.domain.genre.Genre;
import com.ioliveira.admin.catalogo.domain.genre.GenreGateway;
import com.ioliveira.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class DeleteGenreUseCaseIT {

    @Autowired
    private DeleteGenreUseCase useCase;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private GenreGateway genreGateway;

    @BeforeEach
    void cleanUp() {
        this.genreRepository.deleteAll();
    }

    @Test
    public void givenAValidGenreId_whenCallsDeleteGenre_shouldDeleteGenre() {
        assertEquals(0, genreRepository.count());

        final Genre genre = genreGateway.create(Genre.newGenre("Ação", true));

        assertEquals(1, genreRepository.count());

        final var expectedId = genre.getId();

        useCase.execute(expectedId.getValue());

        assertEquals(0, genreRepository.count());
    }

}
