package com.ioliveira.admin.catalogo.e2e;

import com.ioliveira.admin.catalogo.ApiTest;
import com.ioliveira.admin.catalogo.domain.Identifier;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberType;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.genre.GenreID;
import com.ioliveira.admin.catalogo.infrastructure.castmember.models.CastMemberResponse;
import com.ioliveira.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import com.ioliveira.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import com.ioliveira.admin.catalogo.infrastructure.category.models.CategoryResponse;
import com.ioliveira.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.ioliveira.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.GenreResponse;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import com.ioliveira.admin.catalogo.infrastructure.json.Json;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface MockDsl {

    MockMvc mvc();

    /**
     * Category
     */

    default ResultActions deleteACategory(final CategoryID id) throws Exception {
        return this.delete("/categories/", id);
    }

    default CategoryID givenACategory(final String name, final String description, final boolean isActive) throws Exception {
        final String id = this.given(
                "/categories",
                new CreateCategoryRequest(name, description, isActive)
        );

        return CategoryID.from(id);
    }

    default ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, "", "", search);
    }

    default ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    default ResultActions listCategories(final int page, final int perPage, final String sort, final String direction, final String search) throws Exception {
        return this.list("/categories", page, perPage, sort, direction, search);
    }

    default CategoryResponse retrieveACategory(final CategoryID id) throws Exception {
        return this.retrieve("/categories/", id, CategoryResponse.class);
    }

    default ResultActions updateACategory(final CategoryID id, final UpdateCategoryRequest request) throws Exception {
        return this.update("/categories/", id, request);
    }

    /**
     * Genre
     */

    default void deleteAGenre(final GenreID id) throws Exception {
        this.delete("/genres/", id);
    }

    default GenreID givenAGenre(final String name, final List<CategoryID> expectedCategories, final boolean isActive) throws Exception {
        final String id = this.given(
                "/genres",
                new CreateGenreRequest(name, mapTo(expectedCategories, CategoryID::getValue), isActive)
        );

        return GenreID.from(id);
    }

    default GenreResponse retrieveAGenre(final GenreID id) throws Exception {
        return this.retrieve("/genres/", id, GenreResponse.class);
    }

    default ResultActions updateAGenre(final GenreID id, final UpdateGenreRequest request) throws Exception {
        return this.update("/genres/", id, request);
    }

    /**
     * Cast Member
     */

    default ResultActions deleteACastMember(final CastMemberID id) throws Exception {
        return this.delete("/cast_members/", id);
    }

    default CastMemberID givenACastMember(final String name, final CastMemberType type) throws Exception {
        final String id = this.given(
                "/cast_members",
                new CreateCastMemberRequest(name, type)
        );

        return CastMemberID.from(id);
    }

    default ResultActions givenACastMemberWithErrors(final String name, final CastMemberType type) throws Exception {
        return this.givenWithErrors(
                "/cast_members",
                new CreateCastMemberRequest(name, type)
        );
    }

    default ResultActions listCastMembers(final int page, final int perPage, final String search) throws Exception {
        return listCastMembers(page, perPage, "", "", search);
    }

    default ResultActions listCastMembers(final int page, final int perPage) throws Exception {
        return listCastMembers(page, perPage, "", "", "");
    }

    default ResultActions listCastMembers(final int page, final int perPage, final String sort, final String direction, final String search) throws Exception {
        return this.list("/cast_members", page, perPage, sort, direction, search);
    }

    default CastMemberResponse retrieveACastMember(final CastMemberID id) throws Exception {
        return this.retrieve("/cast_members/", id, CastMemberResponse.class);
    }

    default ResultActions retrieveACastMemberWithErrors(final CastMemberID id) throws Exception {
        return this.retrieveWithErrors("/cast_members/", id);
    }

    default ResultActions updateACastMember(final CastMemberID id, final UpdateCastMemberRequest request) throws Exception {
        return this.update("/cast_members/", id, request);
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
                .with(ApiTest.ADMIN_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        return Objects.requireNonNull(
                this.mvc().perform(request)
                        .andDo(print())
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getHeader("Location")
        ).replace("%s/".formatted(url), "");
    }

    private ResultActions givenWithErrors(final String url, final Object body) throws Exception {
        final var request = post(url)
                .with(ApiTest.ADMIN_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        return this.mvc().perform(request)
                .andDo(print());
    }

    private ResultActions list(final String url, final int page, final int perPage, final String sort, final String direction, final String search) throws Exception {

        final var request = get(url)
                .with(ApiTest.ADMIN_JWT)
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("sort", sort)
                .queryParam("dir", direction)
                .queryParam("search", search);

        return this.mvc().perform(request);
    }

    private <T> T retrieve(final String url, final Identifier id, final Class<T> clazz) throws Exception {

        final var request = get(url + id.getValue())
                .with(ApiTest.ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        final var json = this.mvc().perform(request)
                .andDo(print())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, clazz);
    }

    private ResultActions retrieveWithErrors(final String url, final Identifier id) throws Exception {

        final var request = get(url + id.getValue())
                .with(ApiTest.ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        return this.mvc().perform(request)
                .andDo(print());
    }

    private ResultActions delete(final String url, final Identifier id) throws Exception {
        final var request = MockMvcRequestBuilders.delete(url + id.getValue())
                .with(ApiTest.ADMIN_JWT);
        return this.mvc().perform(request);
    }

    private ResultActions update(final String url, final Identifier id, final Object body) throws Exception {
        final var request = put(url + id.getValue())
                .with(ApiTest.ADMIN_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        return this.mvc().perform(request);
    }

}
