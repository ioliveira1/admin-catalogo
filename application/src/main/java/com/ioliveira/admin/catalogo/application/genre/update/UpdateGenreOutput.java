package com.ioliveira.admin.catalogo.application.genre.update;

import com.ioliveira.admin.catalogo.domain.genre.Genre;

public record UpdateGenreOutput(String id) {
    public static UpdateGenreOutput from(final Genre genre) {
        return new UpdateGenreOutput(genre.getId().getValue());
    }
}
