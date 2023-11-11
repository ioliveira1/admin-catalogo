package com.ioliveira.admin.catalogo.infrastructure.api.controllers;

import com.ioliveira.admin.catalogo.application.genre.create.CreateGenreCommand;
import com.ioliveira.admin.catalogo.application.genre.create.CreateGenreOutput;
import com.ioliveira.admin.catalogo.application.genre.create.CreateGenreUseCase;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.infrastructure.api.GenreAPI;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.GenreListResponse;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.GenreResponse;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class GenreController implements GenreAPI {

    private final CreateGenreUseCase createGenreUseCase;

    public GenreController(final CreateGenreUseCase createGenreUseCase) {
        this.createGenreUseCase = Objects.requireNonNull(createGenreUseCase);
    }

    @Override
    public ResponseEntity<?> createGenre(final CreateGenreRequest input) {

        final CreateGenreCommand command =
                CreateGenreCommand.with(input.name(), input.isActive(), input.categories());

        final CreateGenreOutput output = this.createGenreUseCase.execute(command);

        return ResponseEntity
                .created(URI.create("/genres/" + output.id()))
                .body(output);
    }

    @Override
    public Pagination<GenreListResponse> listGenres(final String search, final int page, final int perPage, final String sort, final String direction) {
        return null;
    }

    @Override
    public GenreResponse getById(final String id) {
        return null;
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateGenreRequest input) {
        return null;
    }

    @Override
    public void deleteById(final String id) {

    }
}
