package com.ioliveira.admin.catalogo.e2e.category;

import com.ioliveira.admin.catalogo.E2ETest;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.infrastructure.category.models.CategoryResponse;
import com.ioliveira.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.ioliveira.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.ioliveira.admin.catalogo.infrastructure.json.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

        assertEquals(1, repository.count());

        final var persistedCategory = retrieveACategory(id.getValue());

        assertEquals(expectedName, persistedCategory.name());
        assertEquals(expectedDescription, persistedCategory.description());
        assertEquals(expectedIsActive, persistedCategory.active());
        assertNotNull(persistedCategory.createdAt());
        assertNotNull(persistedCategory.updatedAt());
        assertNull(persistedCategory.deletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToNavigateThroughAllCategories() throws Exception {
        assertEquals(0, repository.count());

        givenACategory("Filmes", null, true);
        givenACategory("Documentários", null, false);
        givenACategory("Séries", null, true);

        assertEquals(3, repository.count());

        listCategories(0, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Documentários")));

        listCategories(1, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(1)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Filmes")));

        listCategories(2, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(2)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Séries")));

        listCategories(3, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(3)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSearchCategories() throws Exception {
        assertEquals(0, repository.count());

        givenACategory("Filmes", null, true);
        givenACategory("Documentários", null, false);
        givenACategory("Séries", null, true);

        assertEquals(3, repository.count());

        listCategories(0, 1, "fil")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(1)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Filmes")));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSortAllCategoriesByDescriptionDesc() throws Exception {
        assertEquals(0, repository.count());

        givenACategory("Filmes", "C", true);
        givenACategory("Documentários", "Z", false);
        givenACategory("Séries", "A", true);

        assertEquals(3, repository.count());

        listCategories(0, 3, "description", "desc", "")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(3)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Documentários")))
                .andExpect(jsonPath("$.items[1].name", equalTo("Filmes")))
                .andExpect(jsonPath("$.items[2].name", equalTo("Séries")));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSeeATreatedErrorWhenCategoryNotFound() throws Exception {

        assertEquals(0, repository.count());

        final var request = get("/categories/123");

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(jsonPath("$.message", equalTo("Category with ID 123 was not found")));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToUpdateACategoryById() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        assertEquals(0, repository.count());

        final var id = givenACategory("Movies", null, true);

        assertEquals(1, repository.count());

        final var requestBody = new UpdateCategoryRequest(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", id.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(requestBody));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

        final var persistedCategory = repository.findById(id.getValue()).get();

        assertEquals(expectedName, persistedCategory.getName());
        assertEquals(expectedDescription, persistedCategory.getDescription());
        assertEquals(expectedIsActive, persistedCategory.isActive());
        assertNotNull(persistedCategory.getCreatedAt());
        assertNotNull(persistedCategory.getUpdatedAt());
        assertNotNull(persistedCategory.getDeletedAt());
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToDeleteACategoryById() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        assertEquals(0, repository.count());

        final var id = givenACategory(expectedName, expectedDescription, expectedIsActive);

        assertEquals(1, repository.count());

        final var request = delete("/categories/{id}", id.getValue());

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent());

        assertEquals(0, repository.count());
    }

    private ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, "", "", search);
    }

    private ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    private ResultActions listCategories(
            final int page,
            final int perPage,
            final String sort,
            final String direction,
            final String search
    ) throws Exception {

        final var request = get("/categories")
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("sort", sort)
                .queryParam("dir", direction)
                .queryParam("search", search);

        return this.mvc.perform(request);
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
