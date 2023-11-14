package com.ioliveira.admin.catalogo.application.castmember.retrieve.list;

import com.ioliveira.admin.catalogo.domain.castmember.CastMemberGateway;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;

import java.util.Objects;

public class DefaultListCastMembersUseCase extends ListCastMembersUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultListCastMembersUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public Pagination<ListCastMemberOutput> execute(final SearchQuery searchQuery) {
        return this.castMemberGateway
                .findAll(searchQuery)
                .map(ListCastMemberOutput::from);
    }
}
