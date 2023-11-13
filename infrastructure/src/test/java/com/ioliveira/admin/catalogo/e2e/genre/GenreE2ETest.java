package com.ioliveira.admin.catalogo.e2e.genre;

import com.ioliveira.admin.catalogo.E2ETest;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.e2e.MockDsl;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import com.ioliveira.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class GenreE2ETest implements MockDsl {

    public static final int ORIGINAL_PORT = 3306;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MockMvc mvc;

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @BeforeEach
    void cleanUp() {
        this.genreRepository.deleteAll();
        this.categoryRepository.deleteAll();
    }

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER =
            new MySQLContainer<>(DockerImageName.parse("mysql:8.2.0"))
                    .withPassword("123456")
                    .withUsername("root")
                    .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add(
                "mysql.port",
                () -> MYSQL_CONTAINER.getMappedPort(ORIGINAL_PORT)
        );
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToCreateANewGenreWithValidValuesAndEmptyCategories() throws Exception {
        final var expectedName = "Ação";
        final var expectedCategories = List.<CategoryID>of();
        final var expectedIsActive = true;

        assertEquals(0, genreRepository.count());

        final var id = givenAGenre(expectedName, expectedCategories, expectedIsActive);

        assertEquals(1, genreRepository.count());

        final var persistedCategory = genreRepository.findById(id.getValue()).get();

        assertEquals(expectedName, persistedCategory.getName());
        assertEquals(sorted(expectedCategories), sorted(persistedCategory.getCategoryIDs())
        );
        assertEquals(expectedIsActive, persistedCategory.isActive());
        assertNotNull(persistedCategory.getCreatedAt());
        assertNotNull(persistedCategory.getUpdatedAt());
        assertNull(persistedCategory.getDeletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToCreateANewGenreWithValidValuesAndCategories() throws Exception {
        final var filmes = givenACategory("Filmes", null, true);

        final var expectedName = "Ação";
        final var expectedCategories = List.of(filmes);
        final var expectedIsActive = true;


        assertEquals(0, genreRepository.count());

        final var id = givenAGenre(expectedName, expectedCategories, expectedIsActive);

        assertEquals(1, genreRepository.count());

        final var persistedCategory = genreRepository.findById(id.getValue()).get();

        assertEquals(expectedName, persistedCategory.getName());
        assertEquals(sorted(expectedCategories), sorted(persistedCategory.getCategoryIDs())
        );
        assertEquals(expectedIsActive, persistedCategory.isActive());
        assertNotNull(persistedCategory.getCreatedAt());
        assertNotNull(persistedCategory.getUpdatedAt());
        assertNull(persistedCategory.getDeletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToNavigateThroughAllGenres() throws Exception {
        assertEquals(0, genreRepository.count());

        givenAGenre("Ação", List.of(), true);
        givenAGenre("Esportes", List.of(), true);
        givenAGenre("Drama", List.of(), true);

        assertEquals(3, genreRepository.count());

        listGenres(0, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Ação")));

        listGenres(1, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(1)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Drama")));

        listGenres(2, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(2)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Esportes")));

        listGenres(3, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(3)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSearchGenres() throws Exception {
        assertEquals(0, genreRepository.count());

        givenAGenre("Ação", List.of(), true);
        givenAGenre("Esportes", List.of(), true);
        givenAGenre("Drama", List.of(), true);

        assertEquals(3, genreRepository.count());

        listGenres(0, 1, "dra")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(1)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Drama")));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSortAllGenresByNameDesc() throws Exception {
        assertEquals(0, genreRepository.count());

        givenAGenre("Ação", List.of(), true);
        givenAGenre("Esportes", List.of(), true);
        givenAGenre("Drama", List.of(), true);

        assertEquals(3, genreRepository.count());

        listGenres(0, 3, "name", "desc", "")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(3)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Esportes")))
                .andExpect(jsonPath("$.items[1].name", equalTo("Drama")))
                .andExpect(jsonPath("$.items[2].name", equalTo("Ação")));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToGetAGenreById() throws Exception {
        assertEquals(0, genreRepository.count());
        assertEquals(0, categoryRepository.count());

        final var filmes = givenACategory("Filmes", null, true);

        final var expectedName = "Ação";
        final var expectedCategories = List.of(filmes);
        final var expectedIsActive = true;

        final var id = givenAGenre(expectedName, expectedCategories, expectedIsActive);

        final var genre = retrieveAGenre(id);

        assertEquals(1, genreRepository.count());
        assertEquals(1, categoryRepository.count());

        assertEquals(expectedName, genre.name());
        assertEquals(expectedIsActive, genre.active());
        assertEquals(sorted(expectedCategories), sorted(genre.categories().stream().map(CategoryID::from).toList()));
        assertNotNull(genre.createdAt());
        assertNotNull(genre.updatedAt());
        assertNull(genre.deletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSeeATreatedErrorWhenGenreNotFound() throws Exception {

        assertEquals(0, genreRepository.count());

        final var request = get("/genres/123");

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(jsonPath("$.message", equalTo("Genre with ID 123 was not found")));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToUpdateAGenreById() throws Exception {
        final var filmes = givenACategory("Filmes", null, true);
        final var expectedName = "Ação";
        final var expectedCategories = List.of(filmes);
        final var expectedIsActive = true;

        assertEquals(0, genreRepository.count());

        final var id = givenAGenre("acccao", expectedCategories, expectedIsActive);

        assertEquals(1, genreRepository.count());

        final var requestBody = new UpdateGenreRequest(
                expectedName,
                mapTo(expectedCategories, CategoryID::getValue),
                expectedIsActive
        );

        updateAGenre(id, requestBody)
                .andDo(print())
                .andExpect(status().isOk());

        final GenreJpaEntity persistedGenre = genreRepository.findById(id.getValue()).get();

        assertEquals(expectedName, persistedGenre.getName());
        assertEquals(sorted(expectedCategories), sorted(persistedGenre.getCategoryIDs()));
        assertEquals(expectedIsActive, persistedGenre.isActive());
        assertNotNull(persistedGenre.getCreatedAt());
        assertNotNull(persistedGenre.getUpdatedAt());
        assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToDeleteAGenreById() throws Exception {
        assertEquals(0, genreRepository.count());

        final var expectedName = "Ação";
        final var expectedCategories = List.<CategoryID>of();
        final var expectedIsActive = true;

        final var id = givenAGenre(expectedName, expectedCategories, expectedIsActive);

        assertEquals(1, genreRepository.count());

        deleteAGenre(id);

        assertEquals(0, genreRepository.count());
    }

    private ResultActions listGenres(final int page, final int perPage, final String search) throws Exception {
        return listGenres(page, perPage, "", "", search);
    }

    private ResultActions listGenres(final int page, final int perPage) throws Exception {
        return listGenres(page, perPage, "", "", "");
    }

    private ResultActions listGenres(
            final int page,
            final int perPage,
            final String sort,
            final String direction,
            final String search
    ) throws Exception {

        final var request = get("/genres")
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("sort", sort)
                .queryParam("dir", direction)
                .queryParam("search", search);

        return this.mvc.perform(request);
    }

}
