package com.ioliveira.admin.catalogo.domain.genre;

import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;

import java.util.Optional;

public interface GenreGateway {

    Genre create(Genre genre);

    void deleteById(GenreID id);

    Optional<Genre> findById(GenreID id);

    Genre update(Genre genre);

    Pagination<Genre> findAll(SearchQuery query);
}
