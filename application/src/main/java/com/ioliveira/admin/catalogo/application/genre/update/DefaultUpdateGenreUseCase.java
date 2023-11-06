package com.ioliveira.admin.catalogo.application.genre.update;

import com.ioliveira.admin.catalogo.domain.Identifier;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import com.ioliveira.admin.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.admin.catalogo.domain.genre.Genre;
import com.ioliveira.admin.catalogo.domain.genre.GenreGateway;
import com.ioliveira.admin.catalogo.domain.genre.GenreID;
import com.ioliveira.admin.catalogo.domain.validation.Error;
import com.ioliveira.admin.catalogo.domain.validation.ValidationHandler;
import com.ioliveira.admin.catalogo.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultUpdateGenreUseCase extends UpdateGenreUseCase {

    private final GenreGateway genreGateway;
    private final CategoryGateway categoryGateway;

    public DefaultUpdateGenreUseCase(
            final GenreGateway genreGateway,
            final CategoryGateway categoryGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public UpdateGenreOutput execute(final UpdateGenreCommand command) {

        final GenreID id = GenreID.from(command.id());
        final List<CategoryID> categories = toCategoryId(command.categories());

        final Genre genre = this.genreGateway
                .findById(id)
                .orElseThrow(notFound(id));

        final Notification notification = Notification.create();
        notification.append(validateCategories(categories));
        notification.validate(() -> genre.update(command.name(), command.isActive(), categories));

        if (notification.hasErrors()) {
            throw new NotificationException("Could not update an aggregate Genre", notification);
        }

        return UpdateGenreOutput.from(this.genreGateway.update(genre));
    }

    private ValidationHandler validateCategories(final List<CategoryID> ids) {
        final Notification notification = Notification.create();
        if (ids == null || ids.isEmpty()) {
            return notification;
        }

        final List<CategoryID> validIds = categoryGateway.existsByIds(ids);

        if (validIds.size() != ids.size()) {
            final ArrayList<CategoryID> inputIds = new ArrayList<>(ids);
            inputIds.removeAll(validIds);

            final String invalidIds = inputIds.stream()
                    .map(CategoryID::getValue)
                    .collect(Collectors.joining(", "));

            notification.append(new Error("Some categories could not be found: %s".formatted(invalidIds)));
        }

        return notification;
    }

    private static Supplier<NotFoundException> notFound(final Identifier id) {
        return () -> NotFoundException.with(Genre.class, id);
    }

    private List<CategoryID> toCategoryId(final List<String> categories) {
        return categories.stream()
                .map(CategoryID::from)
                .toList();
    }
}
