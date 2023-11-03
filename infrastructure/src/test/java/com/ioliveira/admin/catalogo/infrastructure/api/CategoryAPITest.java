package com.ioliveira.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ioliveira.admin.catalogo.ControllerTest;
import com.ioliveira.admin.catalogo.application.category.create.CreateCategoryOutput;
import com.ioliveira.admin.catalogo.application.category.create.CreateCategoryUseCase;
import com.ioliveira.admin.catalogo.application.category.delete.DeleteCategoryUseCase;
import com.ioliveira.admin.catalogo.application.category.retrieve.get.CategoryOutput;
import com.ioliveira.admin.catalogo.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.ioliveira.admin.catalogo.application.category.retrieve.list.CategoryListOutput;
import com.ioliveira.admin.catalogo.application.category.retrieve.list.ListCategoriesUseCase;
import com.ioliveira.admin.catalogo.application.category.update.UpdateCategoryOutput;
import com.ioliveira.admin.catalogo.application.category.update.UpdateCategoryUseCase;
import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.exceptions.DomainException;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.domain.validation.Error;
import com.ioliveira.admin.catalogo.domain.validation.handler.Notification;
import com.ioliveira.admin.catalogo.infrastructure.category.models.CreateCategoryApiInput;
import com.ioliveira.admin.catalogo.infrastructure.category.models.UpdateCategoryApiInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static io.vavr.API.Left;
import static io.vavr.API.Right;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
public class CategoryAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockBean
    private GetCategoryByIdUseCase getCategoryByIdUseCase;

    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @MockBean
    private ListCategoriesUseCase listCategoriesUseCase;

    @Test
    public void givenAValidCommand_WhenCallsCreateCategoryApi_ShouldReturnCategoryId() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var createCategoryApiInput =
                new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenReturn(Right(CreateCategoryOutput.from("123")));

        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(createCategoryApiInput));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/123"))
                .andExpect(jsonPath("$.id", equalTo("123")));

        verify(createCategoryUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAnInvalidName_WhenCallsCreateCategoryApi_ShouldReturnNotification() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var createCategoryApiInput =
                new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenReturn(Left(Notification.create(new Error(expectedErrorMessage))));

        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(createCategoryApiInput));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCategoryUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAnInvalidName_WhenCallsCreateCategoryApi_ShouldReturnDomainException() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var createCategoryApiInput =
                new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenThrow(DomainException.with(new Error(expectedErrorMessage)));

        final var request = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(createCategoryApiInput));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createCategoryUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAValidId_whenCallsGetCategoryApi_shouldReturnCategory() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final Category category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final var expectedId = category.getId().getValue();

        when(getCategoryByIdUseCase.execute(any()))
                .thenReturn(CategoryOutput.from(category));

        final var request = get("/categories/{id}", expectedId);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                .andExpect(jsonPath("$.is_active", equalTo(expectedIsActive)))
                .andExpect(jsonPath("$.created_at", equalTo(category.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(category.getUpdatedAt().toString())))
                .andExpect(jsonPath("$.deleted_at", equalTo(category.getDeletedAt())));
    }

    @Test
    public void givenAnInvalidId_whenCallsGetCategoryApi_shouldReturnNotFound() throws Exception {
        final var expectedErrorMessage = "Category with ID 123 was not found";
        final var expectedId = CategoryID.from("123");

        when(getCategoryByIdUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Category.class, expectedId));

        final var request = get("/categories/{id}", expectedId.getValue());

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAValidCommand_WhenCallsUpdateCategoryApi_ShouldReturnCategoryId() throws Exception {
        final var expectedId = "123";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        when(updateCategoryUseCase.execute(any()))
                .thenReturn(Right(UpdateCategoryOutput.from(expectedId)));

        final var input = new UpdateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(updateCategoryUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAnInvalidId_WhenCallsUpdateCategoryApi_ShouldReturnNotFound() throws Exception {
        final var expectedId = "not-found";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedErrorMessage = "Category with ID not-found was not found";

        when(updateCategoryUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Category.class, CategoryID.from(expectedId)));

        final var input = new UpdateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(updateCategoryUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAnInvalidName_WhenCallsUpdateCategoryApi_ShouldReturnDomainException() throws Exception {
        final var expectedId = "123";
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        when(updateCategoryUseCase.execute(any()))
                .thenReturn(Left(Notification.create(new Error(expectedErrorMessage))));

        final var input = new UpdateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        final var request = put("/categories/{id}", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateCategoryUseCase, times(1)).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAValidId_whenCallsDeleteCategoryApi_shouldReturnNoContent() throws Exception {
        final var expectedId = "123";

        doNothing().when(deleteCategoryUseCase).execute(any());

        final var request = delete("/categories/{id}", expectedId);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(deleteCategoryUseCase, times(1)).execute(expectedId);
    }

    @Test
    public void givenAValidParam_whenCallsListCategoriesApi_shouldReturnCategories() throws Exception {
        final var category = Category.newCategory("Movies", null, true);

        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "movies";
        final var expectedSort = "description";
        final var expectedDirection = "desc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(CategoryListOutput.from(category));

        when(listCategoriesUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        final var request = get("/categories")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", equalTo(hasSize(expectedItemsCount))))
                .andExpect(jsonPath("$.items[0].id", equalTo(category.getId())))
                .andExpect(jsonPath("$.items[0].name", equalTo(category.getName())))
                .andExpect(jsonPath("$.items[0].description", equalTo(category.getDescription())))
                .andExpect(jsonPath("$.items[0].is_active", equalTo(category.isActive())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(category.getCreatedAt())))
                .andExpect(jsonPath("$.items[0].updated_at", equalTo(category.getUpdatedAt())))
                .andExpect(jsonPath("$.items[0].deleted_at", equalTo(category.getDeletedAt())));

        verify(listCategoriesUseCase, times(1)).execute(argThat(query ->
                Objects.equals(expectedPage, query.page())
                        && Objects.equals(expectedPerPage, query.perPage())
                        && Objects.equals(expectedDirection, query.direction())
                        && Objects.equals(expectedSort, query.sort())
                        && Objects.equals(expectedTerms, query.terms())
        ));
    }
}
