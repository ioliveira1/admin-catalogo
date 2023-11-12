package com.ioliveira.admin.catalogo.e2e;

import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.genre.GenreID;
import com.ioliveira.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.ioliveira.admin.catalogo.infrastructure.json.Json;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface MockDsl {

    MockMvc mvc();

    default CategoryID givenACategory(final String name, final String description, final boolean isActive) throws Exception {
        final String id = this.given(
                "/categories",
                new CreateCategoryRequest(name, description, isActive)
        );

        return CategoryID.from(id);
    }

    default GenreID givenAGenre(final String name, final List<CategoryID> expectedCategories, final boolean isActive) throws Exception {
        final String id = this.given(
                "/genres",
                new CreateGenreRequest(name, mapTo(expectedCategories, CategoryID::getValue), isActive)
        );

        return GenreID.from(id);
    }

    default <IN, OUT> List<OUT> mapTo(final List<IN> list, final Function<IN, OUT> mapper) {
        return list.stream()
                .map(mapper)
                .toList();
    }

    default List<CategoryID> sorted(final List<CategoryID> categories) {
        return categories.stream()
                .sorted(Comparator.comparing(CategoryID::getValue))
                .toList();
    }

    private String given(final String url, final Object body) throws Exception {
        final var request = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        return Objects.requireNonNull(
                this.mvc().perform(request)
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getHeader("Location")
        ).replace("%s/".formatted(url), "");
    }

}
