package com.ioliveira.admin.catalogo.application.category.update;

import com.ioliveira.admin.catalogo.IntegrationTest;
import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class UpdateCategoryUseCaseIT {

    @Autowired
    private UpdateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @BeforeEach
    void cleanUp() {
        this.repository.deleteAll();
    }

    @Test
    public void givenAValidCommand_WhenCallsUpdateCategoryUseCase_ShouldReturnCategoryId() {
        final var category = Category.newCategory("name", "desc", true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = category.getId();

        assertEquals(0, repository.count());

        repository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, repository.count());

        final var command = UpdateCategoryCommand
                .with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        final var output = useCase.execute(command).get();

        final var persistedCategory = repository.findById(output.id().getValue()).get();

        assertEquals(expectedName, persistedCategory.getName());
        assertEquals(expectedDescription, persistedCategory.getDescription());
        assertEquals(expectedIsActive, persistedCategory.isActive());
        assertEquals(category.getCreatedAt(), persistedCategory.getCreatedAt());
        assertTrue(category.getUpdatedAt().isBefore(persistedCategory.getUpdatedAt()));
        assertEquals(category.getDeletedAt(), persistedCategory.getDeletedAt());
    }
}
