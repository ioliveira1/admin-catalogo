package com.ioliveira.admin.catalogo.infrastructure.genre;

import com.ioliveira.admin.catalogo.domain.genre.Genre;
import com.ioliveira.admin.catalogo.domain.genre.GenreGateway;
import com.ioliveira.admin.catalogo.domain.genre.GenreID;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class GenreMySQLGateway implements GenreGateway {

    private final GenreRepository genreRepository;

    public GenreMySQLGateway(final GenreRepository genreRepository) {
        this.genreRepository = Objects.requireNonNull(genreRepository);
    }

    @Override
    public Genre create(final Genre genre) {
        return this.genreRepository
                .save(GenreJpaEntity.from(genre))
                .toAggregate();
    }

    @Override
    public void deleteById(final GenreID genreID) {
        final String id = genreID.getValue();

        if (genreRepository.existsById(id)) {
            this.genreRepository.deleteById(id);
        }
    }

    @Override
    public Optional<Genre> findById(final GenreID id) {
        return this.genreRepository
                .findById(id.getValue())
                .map(GenreJpaEntity::toAggregate);
    }

    @Override
    public Genre update(final Genre genre) {
        return this.genreRepository
                .save(GenreJpaEntity.from(genre))
                .toAggregate();
    }

    @Override
    public Pagination<Genre> findAll(final SearchQuery query) {
        return null;
    }
}
