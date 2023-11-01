package com.ioliveira.admin.catalogo.application.category.retrieve.get;

import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetCategoryByIdUseCaseUnitTest {

    @InjectMocks
    private DefaultGetCategoryByIdUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        reset(categoryGateway);
    }

    @Test
    public void givenAValidId_whenCallsGetCategoryUseCase_shouldReturnCategory() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final Category category = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        final CategoryID expectedId = category.getId();

        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        final var output = useCase.execute(expectedId.getValue());

        assertEquals(expectedId, output.id());
        assertEquals(expectedName, output.name());
        assertEquals(expectedDescription, output.description());
        assertEquals(expectedIsActive, output.isActive());
        assertEquals(category.getCreatedAt(), output.createdAt());
        assertEquals(category.getUpdatedAt(), output.updatedAt());
        assertEquals(category.getDeletedAt(), output.deletedAt());
    }

    @Test
    public void givenAnInvalidId_whenCallsGetCategoryUseCase_shouldReturnNotFound() {
        final var expectedErrorMessage = "Category with ID 123 was not found";
        final CategoryID expectedId = CategoryID.from("123");

        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.empty());

        final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(expectedId.getValue()));

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void givenAValidId_whenGatewayThrowsException_shouldReturnException() {
        final var expectedErrorMessage = "Gateway error";
        final CategoryID expectedId = CategoryID.from("123");

        when(categoryGateway.findById(eq(expectedId)))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
