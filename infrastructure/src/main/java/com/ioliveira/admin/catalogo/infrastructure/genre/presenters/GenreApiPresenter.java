package com.ioliveira.admin.catalogo.infrastructure.genre.presenters;

import com.ioliveira.admin.catalogo.application.genre.retreieve.get.GenreOutput;
import com.ioliveira.admin.catalogo.application.genre.retreieve.list.GenreListOutput;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.GenreListResponse;
import com.ioliveira.admin.catalogo.infrastructure.genre.models.GenreResponse;

public interface GenreApiPresenter {

    static GenreResponse presenter(final GenreOutput genreOutput) {
        return new GenreResponse(
                genreOutput.id().getValue(),
                genreOutput.name(),
                genreOutput.categories(),
                genreOutput.isActive(),
                genreOutput.createdAt(),
                genreOutput.updatedAt(),
                genreOutput.deletedAt()
        );
    }

    static GenreListResponse presenter(final GenreListOutput genreListOutput) {
        return new GenreListResponse(
                genreListOutput.id(),
                genreListOutput.name(),
                genreListOutput.isActive(),
                genreListOutput.createdAt(),
                genreListOutput.deletedAt()
        );
    }
}
