package com.ioliveira.admin.catalogo.infrastructure.category.presenters;

import com.ioliveira.admin.catalogo.application.category.retrieve.get.CategoryOutput;
import com.ioliveira.admin.catalogo.infrastructure.category.models.CategoryApiOutput;

public interface CategoryApiPresenter {

    static CategoryApiOutput presenter(final CategoryOutput categoryOutput) {
        return new CategoryApiOutput(
                categoryOutput.id().getValue(),
                categoryOutput.name(),
                categoryOutput.description(),
                categoryOutput.isActive(),
                categoryOutput.createdAt(),
                categoryOutput.updatedAt(),
                categoryOutput.deletedAt()
        );
    }
}
