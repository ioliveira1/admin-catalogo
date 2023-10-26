package com.ioliveira.admin.catalogo.application.category.retrieve.list;

import com.ioliveira.admin.catalogo.application.UseCase;
import com.ioliveira.admin.catalogo.domain.category.CategorySearchQuery;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;

public abstract class ListCategoriesUseCase extends UseCase<CategorySearchQuery, Pagination<CategoryListOutput>> {
}
