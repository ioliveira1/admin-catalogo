package com.ioliveira.admin.catalogo.infrastructure.category.presenters;

import com.ioliveira.admin.catalogo.application.category.retrieve.get.CategoryOutput;
import com.ioliveira.admin.catalogo.application.category.retrieve.list.CategoryListOutput;
import com.ioliveira.admin.catalogo.infrastructure.category.models.CategoryResponse;
import com.ioliveira.admin.catalogo.infrastructure.category.models.CategoryListResponse;

public interface CategoryApiPresenter {

    static CategoryResponse presenter(final CategoryOutput categoryOutput) {
        return new CategoryResponse(
                categoryOutput.id().getValue(),
                categoryOutput.name(),
                categoryOutput.description(),
                categoryOutput.isActive(),
                categoryOutput.createdAt(),
                categoryOutput.updatedAt(),
                categoryOutput.deletedAt()
        );
    }

    static CategoryListResponse presenter(final CategoryListOutput categoryListOutput) {
        return new CategoryListResponse(
                categoryListOutput.id().getValue(),
                categoryListOutput.name(),
                categoryListOutput.description(),
                categoryListOutput.isActive(),
                categoryListOutput.createdAt(),
                categoryListOutput.deletedAt()
        );
    }
}
