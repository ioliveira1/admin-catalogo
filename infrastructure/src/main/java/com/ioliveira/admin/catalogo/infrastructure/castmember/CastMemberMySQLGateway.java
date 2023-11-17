package com.ioliveira.admin.catalogo.infrastructure.castmember;

import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import com.ioliveira.admin.catalogo.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class CastMemberMySQLGateway implements CastMemberGateway {

    private final CastMemberRepository repository;

    public CastMemberMySQLGateway(final CastMemberRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public CastMember create(final CastMember castMember) {
        return this.repository
                .save(CastMemberJpaEntity.from(castMember))
                .toAggregate();
    }

    @Override
    public void deleteById(final CastMemberID id) {
        final String idValue = id.getValue();
        if (this.repository.existsById(idValue)) {
            this.repository.deleteById(idValue);
        }
    }

    @Override
    public Optional<CastMember> findById(final CastMemberID id) {
        return this.repository
                .findById(id.getValue())
                .map(CastMemberJpaEntity::toAggregate);
    }

    @Override
    public CastMember update(final CastMember castMember) {
        return this.repository
                .save(CastMemberJpaEntity.from(castMember))
                .toAggregate();
    }

    @Override
    public Pagination<CastMember> findAll(final SearchQuery query) {
        final PageRequest page = PageRequest.of(
                query.page(),
                query.perPage(),
                Sort.by(Sort.Direction.fromString(query.direction()), query.sort())
        );

        final Specification<CastMemberJpaEntity> specification = Optional.ofNullable(query.terms())
                .filter(str -> !str.isBlank())
                .map(this::specification)
                .orElse(null);

        final Page<CastMemberJpaEntity> pageResult = this.repository.findAll(specification, page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CastMemberJpaEntity::toAggregate).toList()
        );
    }

    private Specification<CastMemberJpaEntity> specification(final String terms) {
        return SpecificationUtils.like("name", terms);
    }
}
