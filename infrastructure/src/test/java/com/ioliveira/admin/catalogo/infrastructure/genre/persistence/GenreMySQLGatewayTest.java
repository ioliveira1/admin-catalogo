package com.ioliveira.admin.catalogo.infrastructure.genre.persistence;

import com.ioliveira.admin.catalogo.MySQLGatewayTest;
import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.genre.Genre;
import com.ioliveira.admin.catalogo.infrastructure.category.CategoryMySQLGateway;
import com.ioliveira.admin.catalogo.infrastructure.genre.GenreMySQLGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@MySQLGatewayTest
public class GenreMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private GenreMySQLGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void cleanUp() {
        this.genreRepository.deleteAll();
    }

    @Test
    public void test() {
        Assertions.assertNotNull(categoryGateway);
        Assertions.assertNotNull(genreGateway);
        Assertions.assertNotNull(genreRepository);
    }

    @Test
    public void givenAValidGenre_whenCallsCreateGenre_shouldPersistGenre() {
        final var filmes =
                categoryGateway.create(Category.newCategory("Filmes", null, true));

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId());

        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        genre.addCategories(expectedCategories);

        final var expectedId = genre.getId();

        assertEquals(0, genreRepository.count());

        final var actualGenre = genreGateway.create(genre);

        assertEquals(1, genreRepository.count());

        assertEquals(expectedId, actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(genre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(genre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertEquals(genre.getDeletedAt(), actualGenre.getDeletedAt());
        assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        assertEquals(genre.getCreatedAt(), persistedGenre.getCreatedAt());
        assertEquals(genre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        assertEquals(genre.getDeletedAt(), persistedGenre.getDeletedAt());
        assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenreWithoutCategories_whenCallsCreateGenre_shouldPersistGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var genre = Genre.newGenre(expectedName, expectedIsActive);

        final var expectedId = genre.getId();

        assertEquals(0, genreRepository.count());

        final var actualGenre = genreGateway.create(genre);

        assertEquals(1, genreRepository.count());

        assertEquals(expectedId, actualGenre.getId());
        assertEquals(expectedName, actualGenre.getName());
        assertEquals(expectedIsActive, actualGenre.isActive());
        assertEquals(expectedCategories, actualGenre.getCategories());
        assertEquals(genre.getCreatedAt(), actualGenre.getCreatedAt());
        assertEquals(genre.getUpdatedAt(), actualGenre.getUpdatedAt());
        assertEquals(genre.getDeletedAt(), actualGenre.getDeletedAt());
        assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertEquals(expectedCategories, persistedGenre.getCategoryIDs());
        assertEquals(genre.getCreatedAt(), persistedGenre.getCreatedAt());
        assertEquals(genre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        assertEquals(genre.getDeletedAt(), persistedGenre.getDeletedAt());
        assertNull(persistedGenre.getDeletedAt());
    }

}
