package com.ioliveira.admin.catalogo.infrastructure.api.controllers;

import com.ioliveira.admin.catalogo.application.genre.create.CreateGenreCommand;
import com.ioliveira.admin.catalogo.application.genre.create.CreateGenreOutput;
import com.ioliveira.admin.catalogo.application.genre.create.CreateGenreUseCase;
import com.ioliveira.admin.catalogo.application.genre.delete.DeleteGenreUseCase;
import com.ioliveira.admin.catalogo.application.genre.retreieve.get.GetGenreByIdUseCase;
import com.ioliveira.admin.catalogo.application.genre.update.UpdateGenreCommand;
import com.ioliveira.admin.catalogo.application.genre.update.UpdateGenreUseCase;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.infrastructure.api.GenreAPI;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.GenreListResponse;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.GenreResponse;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import com.ioliveira.admin.catalogo.infrastructure.genre.presenters.GenreApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class GenreController implements GenreAPI {

    private final CreateGenreUseCase createGenreUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final UpdateGenreUseCase updateGenreUseCase;
    private final DeleteGenreUseCase deleteGenreUseCase;

    public GenreController(
            final CreateGenreUseCase createGenreUseCase,
            final GetGenreByIdUseCase getGenreByIdUseCase,
            final UpdateGenreUseCase updateGenreUseCase,
            final DeleteGenreUseCase deleteGenreUseCase) {

        this.createGenreUseCase = Objects.requireNonNull(createGenreUseCase);
        this.getGenreByIdUseCase = Objects.requireNonNull(getGenreByIdUseCase);
        this.updateGenreUseCase = Objects.requireNonNull(updateGenreUseCase);
        this.deleteGenreUseCase = Objects.requireNonNull(deleteGenreUseCase);
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
        return GenreApiPresenter
                .presenter(this.getGenreByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateGenreRequest input) {

        final UpdateGenreCommand command =
                UpdateGenreCommand.with(id, input.name(), input.isActive(), input.categories());

        return ResponseEntity.ok(this.updateGenreUseCase.execute(command));
    }

    @Override
    public void deleteById(final String id) {
        this.deleteGenreUseCase.execute(id);
    }
}
