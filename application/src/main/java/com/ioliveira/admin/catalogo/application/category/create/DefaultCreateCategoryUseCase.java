package com.ioliveira.admin.catalogo.application.category.create;

import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;

import java.util.Objects;

public class DefaultCreateCategoryUseCase extends CreateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultCreateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public CreateCategoryOutput execute(final CreateCategoryCommand createCategoryCommand) {

        final Category category = Category
                .newCategory(
                        createCategoryCommand.name(),
                        createCategoryCommand.description(),
                        createCategoryCommand.isActive()
                );

        category.validate(new ThrowsValidationHandler());

        return CreateCategoryOutput
                .from(this.categoryGateway.create(category));
    }
}
