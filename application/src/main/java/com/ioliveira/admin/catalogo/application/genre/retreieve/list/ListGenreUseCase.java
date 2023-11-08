package com.ioliveira.admin.catalogo.application.genre.retreieve.list;

import com.ioliveira.admin.catalogo.application.UseCase;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;

public abstract class ListGenreUseCase extends UseCase<SearchQuery, Pagination<GenreListOutput>> {
}
