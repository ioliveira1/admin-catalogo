package com.ioliveira.admin.catalogo.domain.category;

import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;

import java.util.List;
import java.util.Optional;

public interface CategoryGateway {

    Category create(Category category);

    void deleteById(CategoryID id);

    Optional<Category> findById(CategoryID id);

    Category update(Category category);

    Pagination<Category> findAll(SearchQuery query);

    List<CategoryID> existsByIds(List<CategoryID> ids);
}
