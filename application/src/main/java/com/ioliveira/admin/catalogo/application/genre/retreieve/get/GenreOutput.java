package com.ioliveira.admin.catalogo.application.genre.retreieve.get;

import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.genre.Genre;
import com.ioliveira.admin.catalogo.domain.genre.GenreID;

import java.time.Instant;
import java.util.List;

public record GenreOutput(
        GenreID id,
        String name,
        boolean isActive,
        List<String> categories,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

    public static GenreOutput from(final Genre genre) {
        return new GenreOutput(
                genre.getId(),
                genre.getName(),
                genre.isActive(),
                genre.getCategories().stream().map(CategoryID::getValue).toList(),
                genre.getCreatedAt(),
                genre.getUpdatedAt(),
                genre.getDeletedAt());
    }
}
