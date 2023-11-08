package com.ioliveira.admin.catalogo.infrastructure.genre.persistence;

import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.genre.Genre;
import com.ioliveira.admin.catalogo.domain.genre.GenreID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name = "genres")
public class GenreJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active", nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "genre", cascade = ALL, fetch = EAGER, orphanRemoval = true)
    private Set<GenreCategoryJpaEntity> categories;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    @Column(name = "deleted_at", columnDefinition = "DATETIME(6)")
    private Instant deletedAt;

    public GenreJpaEntity() {
    }

    public GenreJpaEntity(
            final String id,
            final String name,
            final boolean isActive,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt
    ) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.categories = new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static GenreJpaEntity from(final Genre genre) {
        final GenreJpaEntity entity = new GenreJpaEntity(
                genre.getId().getValue(),
                genre.getName(),
                genre.isActive(),
                genre.getCreatedAt(),
                genre.getUpdatedAt(),
                genre.getDeletedAt()
        );

        genre.getCategories().forEach(entity::addCategory);

        return entity;

    }

    public Genre toAggregate() {
        return Genre.with(
                GenreID.from(getId()),
                getName(),
                isActive(),
                getCategories().stream()
                        .map(x -> CategoryID.from(x.getId().getCategoryId()))
                        .toList(),
                getCreatedAt(),
                getUpdatedAt(),
                getDeletedAt()
        );
    }

    private void addCategory(final CategoryID id) {
        this.categories.add(GenreCategoryJpaEntity.from(this, id));
    }

    private void removeCategory(final CategoryID id) {
        this.categories.remove(GenreCategoryJpaEntity.from(this, id));
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(final boolean active) {
        isActive = active;
    }

    public Set<GenreCategoryJpaEntity> getCategories() {
        return categories;
    }

    public void setCategories(final Set<GenreCategoryJpaEntity> categories) {
        this.categories = categories;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(final Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
