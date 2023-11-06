package com.ioliveira.admin.catalogo.application.category.retrieve.list;

import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;

import java.util.Objects;

public class DefaultListCategoriesUseCase extends ListCategoriesUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultListCategoriesUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Pagination<CategoryListOutput> execute(final SearchQuery query) {
        return this.categoryGateway.findAll(query).map(CategoryListOutput::from);
    }
}
