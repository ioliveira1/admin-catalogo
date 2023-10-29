package com.ioliveira.admin.catalogo.infrastructure.category;

import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.category.CategorySearchQuery;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryRepository repository;

    public CategoryMySQLGateway(final CategoryRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Category create(final Category category) {
        return this.repository
                .save(CategoryJpaEntity.from(category))
                .toAggregate();
    }

    @Override
    public void deleteById(final CategoryID id) {

    }

    @Override
    public Optional<Category> findById(final CategoryID id) {
        return Optional.empty();
    }

    @Override
    public Category update(final Category category) {
        return this.repository
                .save(CategoryJpaEntity.from(category))
                .toAggregate();
    }

    @Override
    public Pagination<Category> findAll(final CategorySearchQuery query) {
        return null;
    }
}
