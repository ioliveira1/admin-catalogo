package com.ioliveira.admin.catalogo.e2e.genre;

import com.ioliveira.admin.catalogo.E2ETest;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.e2e.MockDsl;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.ioliveira.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

}
