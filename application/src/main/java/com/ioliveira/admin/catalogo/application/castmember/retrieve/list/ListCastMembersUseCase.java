package com.ioliveira.admin.catalogo.application.castmember.retrieve.list;

import com.ioliveira.admin.catalogo.application.UseCase;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;

public abstract class ListCastMembersUseCase extends UseCase<SearchQuery, Pagination<ListCastMemberOutput>> {
}
