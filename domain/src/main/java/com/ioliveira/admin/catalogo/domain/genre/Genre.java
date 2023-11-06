package com.ioliveira.admin.catalogo.domain.genre;

import com.ioliveira.admin.catalogo.domain.AggregateRoot;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotificationException;
import com.ioliveira.admin.catalogo.domain.utils.InstantUtils;
import com.ioliveira.admin.catalogo.domain.validation.ValidationHandler;
import com.ioliveira.admin.catalogo.domain.validation.handler.Notification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Genre extends AggregateRoot<GenreID> {

    private String name;
    private boolean active;
    private List<CategoryID> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private Genre(
            final GenreID id,
            final String name,
            final boolean active,
            final List<CategoryID> categories,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt) {

        super(id);
        this.name = name;
        this.active = active;
        this.categories = categories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;

        selfValidate();
    }

    public static Genre newGenre(final String name, final boolean isActive) {
        final GenreID id = GenreID.unique();
        final Instant now = InstantUtils.now();

        return new Genre(id,
                name,
                isActive,
                new ArrayList<>(),
                now,
                now,
                isActive ? null : now
        );
    }

    public Genre update(final String name, final boolean isActive, final List<CategoryID> categories) {
        if (isActive) {
            activate();
        } else {
            deactivate();
        }
        this.name = name;
        this.categories = new ArrayList<>(categories != null ? categories : Collections.emptyList());
        this.updatedAt = InstantUtils.now();
        selfValidate();
        return this;
    }

    public Genre activate() {
        this.deletedAt = null;
        this.active = true;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre deactivate() {
        if (getDeletedAt() == null) {
            this.deletedAt = InstantUtils.now();
        }
        this.active = false;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public static Genre clone(final Genre genre) {
        return new Genre(
                genre.id,
                genre.getName(),
                genre.isActive(),
                new ArrayList<>(genre.getCategories()),
                genre.getCreatedAt(),
                genre.getUpdatedAt(),
                genre.getDeletedAt()
        );
    }

    private void selfValidate() {
        final Notification notification = Notification.create();
        validate(notification);

        if (notification.hasErrors()) {
            throw new NotificationException("Failed to create an aggregate Genre", notification);
        }
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new GenreValidator(handler, this).validate();
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public List<CategoryID> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}
