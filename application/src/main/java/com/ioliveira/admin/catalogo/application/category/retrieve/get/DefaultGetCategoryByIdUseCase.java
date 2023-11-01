package com.ioliveira.admin.catalogo.application.category.retrieve.get;

import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetCategoryByIdUseCase extends GetCategoryByIdUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultGetCategoryByIdUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public CategoryOutput execute(final String id) {
        final CategoryID categoryID = CategoryID.from(id);

        return this.categoryGateway
                .findById(categoryID)
                .map(CategoryOutput::from)
                .orElseThrow(notFound(categoryID));
    }

    private static Supplier<NotFoundException> notFound(final CategoryID id) {
        return () -> NotFoundException.with(Category.class, id);
    }
}
