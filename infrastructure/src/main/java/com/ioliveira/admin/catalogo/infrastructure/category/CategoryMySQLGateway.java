package com.ioliveira.admin.catalogo.infrastructure.category;

import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.ioliveira.admin.catalogo.infrastructure.utils.SpecificationUtils.like;

@Component
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
    public void deleteById(final CategoryID categoryID) {
        final String idValue = categoryID.getValue();

        if (this.repository.existsById(idValue)) {
            this.repository.deleteById(idValue);
        }
    }

    @Override
    public Optional<Category> findById(final CategoryID id) {
        return this.repository
                .findById(id.getValue())
                .map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category update(final Category category) {
        return this.repository
                .save(CategoryJpaEntity.from(category))
                .toAggregate();
    }

    @Override
    public Pagination<Category> findAll(final SearchQuery query) {

        final PageRequest pageRequest = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(
                        Sort.Direction.fromString(query.direction()),
                        query.sort()
                )
        );

        final Specification<CategoryJpaEntity> specification = Optional.ofNullable(query.terms())
                .filter(str -> !str.isBlank())
                .map(this::specification)
                .orElse(null);

        final Page<CategoryJpaEntity> pageResult = this.repository.findAll(Specification.where(specification), pageRequest);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CategoryJpaEntity::toAggregate).toList()
        );
    }

    @Override
    public List<CategoryID> existsByIds(final List<CategoryID> ids) {
        //TODO implementar quando chegar na camada de infra
        return Collections.emptyList();
    }

    private Specification<CategoryJpaEntity> specification(final String term) {
        final Specification<CategoryJpaEntity> nameLike = like("name", term);
        final Specification<CategoryJpaEntity> descriptionLike = like("description", term);
        return nameLike.or(descriptionLike);
    }
}
