package com.ioliveira.admin.catalogo.infrastructure.genre;

import com.ioliveira.admin.catalogo.domain.genre.Genre;
import com.ioliveira.admin.catalogo.domain.genre.GenreGateway;
import com.ioliveira.admin.catalogo.domain.genre.GenreID;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GenreMySQLGateway implements GenreGateway {

    @Override
    public Genre create(final Genre genre) {
        return null;
    }

    @Override
    public void deleteById(final GenreID id) {

    }

    @Override
    public Optional<Genre> findById(final GenreID id) {
        return Optional.empty();
    }

    @Override
    public Genre update(final Genre genre) {
        return null;
    }

    @Override
    public Pagination<Genre> findAll(final SearchQuery query) {
        return null;
    }
}
