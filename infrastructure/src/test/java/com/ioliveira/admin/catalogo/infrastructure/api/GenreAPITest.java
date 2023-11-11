package com.ioliveira.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ioliveira.admin.catalogo.ControllerTest;
import com.ioliveira.admin.catalogo.application.genre.create.CreateGenreOutput;
import com.ioliveira.admin.catalogo.application.genre.create.CreateGenreUseCase;
import com.ioliveira.admin.catalogo.application.genre.retreieve.get.GenreOutput;
import com.ioliveira.admin.catalogo.application.genre.retreieve.get.GetGenreByIdUseCase;
import com.ioliveira.admin.catalogo.application.genre.update.UpdateGenreOutput;
import com.ioliveira.admin.catalogo.application.genre.update.UpdateGenreUseCase;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.admin.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.admin.catalogo.domain.genre.Genre;
import com.ioliveira.admin.catalogo.domain.genre.GenreID;
import com.ioliveira.admin.catalogo.domain.validation.Error;
import com.ioliveira.admin.catalogo.domain.validation.handler.Notification;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = GenreAPI.class)
public class GenreAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateGenreUseCase createGenreUseCase;

    @MockBean
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @MockBean
    private UpdateGenreUseCase updateGenreUseCase;

    @Test
    public void givenAValidCommand_WhenCallsCreateGenreApi_ShouldReturnGenreId() throws Exception {
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123", "456");
        final var expectedIsActive = true;

        final var createGenreRequest =
                new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        when(createGenreUseCase.execute(any()))
                .thenReturn(CreateGenreOutput.from("123"));

        final var request = post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(createGenreRequest));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/genres/123"))
                .andExpect(jsonPath("$.id", equalTo("123")));

        verify(createGenreUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(sorted(expectedCategories), sorted(cmd.categories()))
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAnInvalidName_WhenCallsCreateGenreApi_ShouldReturnNotification() throws Exception {
        final var expectedCategories = List.of("123", "456");
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";

        final var createGenreRequest =
                new CreateGenreRequest(null, expectedCategories, expectedIsActive);

        when(createGenreUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));

        final var request = post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(createGenreRequest));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createGenreUseCase).execute(argThat(cmd ->
                Objects.equals(null, cmd.name())
                        && Objects.equals(sorted(expectedCategories), sorted(cmd.categories()))
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    @Test
    public void givenAValidId_whenCallsGetGenreApi_shouldReturnGenre() throws Exception {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.of("123", "456");

        final Genre genre = Genre
                .newGenre(expectedName, expectedIsActive)
                .addCategories(
                        expectedCategories.stream()
                                .map(CategoryID::from)
                                .toList()
                );

        final var expectedId = genre.getId().getValue();

        when(getGenreByIdUseCase.execute(any()))
                .thenReturn(GenreOutput.from(genre));

        final var request = get("/genres/{id}", expectedId);

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.name", equalTo(expectedName)))
                .andExpect(jsonPath("$.categories_id", equalTo(expectedCategories)))
                .andExpect(jsonPath("$.is_active", equalTo(genre.isActive())))
                .andExpect(jsonPath("$.created_at", equalTo(genre.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(genre.getUpdatedAt().toString())))
                .andExpect(jsonPath("$.deleted_at", equalTo(genre.getDeletedAt().toString())));

        verify(getGenreByIdUseCase).execute(eq(expectedId));
    }

    @Test
    public void givenAnInvalidId_whenCallsGetGenreApi_shouldReturnNotFound() throws Exception {
        final var expectedErrorMessage = "Genre with ID 123 was not found";

        final var expectedId = GenreID.from("123");

        when(getGenreByIdUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Genre.class, expectedId));

        final var request = get("/genres/{id}", expectedId.getValue());

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

        verify(getGenreByIdUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenAValidCommand_WhenCallsUpdateGenreApi_ShouldReturnCategoryId() throws Exception {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of("123", "456");

        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        final var expectedId = genre.getId();

        final var updateGenreRequest = new UpdateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        when(updateGenreUseCase.execute(any()))
                .thenReturn(UpdateGenreOutput.from(genre));

        final var request = put("/genres/{id}", expectedId)
                .content(this.mapper.writeValueAsString(updateGenreRequest));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(expectedId)));

        verify(updateGenreUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(sorted(expectedCategories), sorted(cmd.categories()))
                        && Objects.equals(expectedIsActive, cmd.isActive())));
    }

    @Test
    public void givenAnInvalidName_WhenCallsUpdateGenreApi_ShouldReturnNotification() throws Exception {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of("123", "456");
        final var expectedErrorMessage = "'name' should not be null";

        final var genre = Genre.newGenre(expectedName, expectedIsActive);
        final var expectedId = genre.getId();

        final var updateGenreRequest = new UpdateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        when(updateGenreUseCase.execute(any()))
                .thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));

        final var request = put("/genres/{id}", expectedId)
                .content(this.mapper.writeValueAsString(updateGenreRequest));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", nullValue()))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(createGenreUseCase).execute(argThat(cmd ->
                Objects.equals(null, cmd.name())
                        && Objects.equals(sorted(expectedCategories), sorted(cmd.categories()))
                        && Objects.equals(expectedIsActive, cmd.isActive())
        ));
    }

    private List<String> sorted(final List<String> categories) {
        return categories.stream().sorted().toList();
    }

}
