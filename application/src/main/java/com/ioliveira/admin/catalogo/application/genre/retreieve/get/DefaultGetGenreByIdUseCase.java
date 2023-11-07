package com.ioliveira.admin.catalogo.application.genre.retreieve.get;

import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.admin.catalogo.domain.genre.Genre;
import com.ioliveira.admin.catalogo.domain.genre.GenreGateway;
import com.ioliveira.admin.catalogo.domain.genre.GenreID;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetGenreByIdUseCase extends GetGenreByIdUseCase {

    private final GenreGateway genreGateway;

    public DefaultGetGenreByIdUseCase(final GenreGateway genreGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public GenreOutput execute(final String id) {
        final GenreID genreID = GenreID.from(id);

        return this.genreGateway
                .findById(genreID)
                .map(GenreOutput::from)
                .orElseThrow(notFound(genreID));
    }

    private static Supplier<NotFoundException> notFound(final GenreID id) {
        return () -> NotFoundException.with(Genre.class, id);
    }
}
