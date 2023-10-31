package com.ioliveira.admin.catalogo.application.category.retrieve.get;

import com.ioliveira.admin.catalogo.IntegrationTest;
import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@IntegrationTest
public class GetCategoryByIdUseCaseIT {

    @Autowired
    private GetCategoryByIdUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @BeforeEach
    void cleanUp() {
        this.repository.deleteAll();
    }

    @Test
    public void givenAValidId_whenCallsGetCategoryUseCase_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final Category category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final CategoryID expectedId = category.getId();

        assertEquals(0, repository.count());

        repository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, repository.count());

        final var output = useCase.execute(expectedId.getValue());

        assertEquals(expectedId, output.id());
        assertEquals(expectedName, output.name());
        assertEquals(expectedDescription, output.description());
        assertEquals(expectedIsActive, output.isActive());
        assertEquals(category.getCreatedAt(), output.createdAt());
        assertEquals(category.getUpdatedAt(), output.updatedAt());
        assertEquals(category.getDeletedAt(), output.deletedAt());
    }
}
