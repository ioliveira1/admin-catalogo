package com.ioliveira.admin.catalogo.application.category.create;

import com.ioliveira.admin.catalogo.IntegrationTest;
import com.ioliveira.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@IntegrationTest
public class CreateCategoryUseCaseIT {

    @Autowired
    private CreateCategoryUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @BeforeEach
    void cleanUp() {
        this.repository.deleteAll();
    }

    @Test
    public void givenAValidCommand_WhenCallsCreateCategoryUseCase_ShouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        assertEquals(0, repository.count());

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final var output = useCase.execute(command).get();

        assertEquals(1, repository.count());

        assertNotNull(output);
        assertNotNull(output.id());

        final var persistedCategory = repository.findById(output.id()).get();

        assertEquals(expectedName, persistedCategory.getName());
        assertEquals(expectedDescription, persistedCategory.getDescription());
        assertEquals(expectedIsActive, persistedCategory.isActive());
        assertNotNull(persistedCategory.getCreatedAt());
        assertNotNull(persistedCategory.getUpdatedAt());
        assertNull(persistedCategory.getDeletedAt());
    }
}
