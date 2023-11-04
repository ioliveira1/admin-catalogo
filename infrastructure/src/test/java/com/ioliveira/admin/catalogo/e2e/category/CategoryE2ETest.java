package com.ioliveira.admin.catalogo.e2e.category;

import com.ioliveira.admin.catalogo.E2ETest;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.infrastructure.category.models.CategoryResponse;
import com.ioliveira.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.ioliveira.admin.catalogo.infrastructure.json.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class CategoryE2ETest {

    public static final int ORIGINAL_PORT = 3306;

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void cleanUp() {
        this.repository.deleteAll();
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
    public void asACatalogAdminIShouldBeAbleToCreateANewCategoryWithValidValues() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        assertEquals(0, repository.count());

        final var id = givenACategory(expectedName, expectedDescription, expectedIsActive);

        final var persistedCategory = retrieveACategory(id.getValue());

        assertEquals(expectedName, persistedCategory.name());
        assertEquals(expectedDescription, persistedCategory.description());
        assertEquals(expectedIsActive, persistedCategory.active());
        assertNotNull(persistedCategory.createdAt());
        assertNotNull(persistedCategory.updatedAt());
        assertNull(persistedCategory.deletedAt());
    }

    private CategoryID givenACategory(final String name, final String description, final boolean isActive) throws Exception {

        final var requestBody = new CreateCategoryRequest(name, description, isActive);

        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        final String id = Objects.requireNonNull(
                        this.mvc.perform(request)
                                .andDo(print())
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getHeader("Location")
                )
                .replace("/categories/", "");

        return CategoryID.from(id);
    }

    private CategoryResponse retrieveACategory(final String id) throws Exception {

        final var request = get("/categories/{id}", id);

        final var json = this.mvc.perform(request)
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, CategoryResponse.class);
    }

}
