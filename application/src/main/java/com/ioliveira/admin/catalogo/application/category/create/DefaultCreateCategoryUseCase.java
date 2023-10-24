package com.ioliveira.admin.catalogo.application.category.create;

import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

import java.util.Objects;

import static io.vavr.API.Left;
import static io.vavr.API.Try;

public class DefaultCreateCategoryUseCase extends CreateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultCreateCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public Either<Notification, CreateCategoryOutput> execute(final CreateCategoryCommand createCategoryCommand) {

        final Category category = Category
                .newCategory(
                        createCategoryCommand.name(),
                        createCategoryCommand.description(),
                        createCategoryCommand.isActive()
                );

        final Notification notification = Notification.create();
        category.validate(notification);

        return notification.hasErrors()
                ? Left(notification)
                : create(category);
    }

    private Either<Notification, CreateCategoryOutput> create(final Category category) {
        return Try(() -> this.categoryGateway.create(category))
                .toEither()
                .bimap(Notification::create, CreateCategoryOutput::from);
    }
}
