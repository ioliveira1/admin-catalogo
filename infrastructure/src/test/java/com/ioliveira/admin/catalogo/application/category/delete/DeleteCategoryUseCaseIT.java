package com.ioliveira.admin.catalogo.application.category.delete;

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
public class DeleteCategoryUseCaseIT {

    @Autowired
    private DeleteCategoryUseCase useCase;

    @Autowired
    private CategoryRepository repository;

    @BeforeEach
    void cleanUp() {
        this.repository.deleteAll();
    }

    @Test
    public void givenAValidId_whenCallsDeleteCategoryUseCase_shouldBeOk() {
        final Category category = Category.newCategory("Filmes", "A categoria mais assistida", true);
        final CategoryID expectedId = category.getId();

        assertEquals(0, repository.count());

        repository.saveAndFlush(CategoryJpaEntity.from(category));

        assertEquals(1, repository.count());

        useCase.execute(expectedId.getValue());

        assertEquals(0, repository.count());
    }
}
