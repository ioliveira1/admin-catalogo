package com.ioliveira.admin.catalogo.application.genre.create;

import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.admin.catalogo.domain.genre.Genre;
import com.ioliveira.admin.catalogo.domain.genre.GenreGateway;
import com.ioliveira.admin.catalogo.domain.validation.Error;
import com.ioliveira.admin.catalogo.domain.validation.ValidationHandler;
import com.ioliveira.admin.catalogo.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultCreateGenreUseCase extends CreateGenreUseCase {

    private final GenreGateway genreGateway;
    private final CategoryGateway categoryGateway;

    public DefaultCreateGenreUseCase(
            final GenreGateway genreGateway,
            final CategoryGateway categoryGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public CreateGenreOutput execute(final CreateGenreCommand command) {

        final List<CategoryID> categories = command.categories().stream().map(CategoryID::from).toList();

        final Notification notification = Notification.create();
        validateCategories(categories, notification);

        final Genre genre = notification.validate(() -> Genre.newGenre(command.name(), command.isActive()));

        if (notification.hasErrors()) {
            throw new NotificationException("Could not create an aggregate Genre", notification);
        }

        genre.addCategories(categories);
        return CreateGenreOutput.from(this.genreGateway.create(genre));
    }

    private ValidationHandler validateCategories(final List<CategoryID> ids, final ValidationHandler handler) {
        if (ids == null || ids.isEmpty()) {
            return handler;
        }

        final List<CategoryID> validIds = categoryGateway.existsByIds(ids);

        if (validIds.size() != ids.size()) {
            final ArrayList<CategoryID> inputIds = new ArrayList<>(ids);
            inputIds.removeAll(validIds);

            final String invalidIds = inputIds.stream()
                    .map(CategoryID::getValue)
                    .collect(Collectors.joining(", "));

            handler.append(new Error("Some categories could not be found: %s".formatted(invalidIds)));
        }

        return handler;
    }
}
