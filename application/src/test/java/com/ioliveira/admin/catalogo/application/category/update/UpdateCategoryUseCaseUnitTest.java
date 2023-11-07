package com.ioliveira.admin.catalogo.application.category.update;

import com.ioliveira.admin.catalogo.application.UseCaseTest;
import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateCategoryUseCaseUnitTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Override
    public List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Test
    public void givenAValidInactiveCommand_WhenCallsUpdateCategoryUseCase_ShouldReturnInactiveCategoryId() {
        final var category = Category.newCategory("name", "desc", true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = category.getId();

        final var command = UpdateCategoryCommand
                .with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        when(categoryGateway.update(any()))
                .thenAnswer(AdditionalAnswers.returnsFirstArg());

        assertTrue(category.isActive());
        assertNull(category.getDeletedAt());

        final var output = useCase.execute(command).get();

        assertNotNull(output);
        assertNotNull(output.id());

        verify(categoryGateway, times(1)).findById(eq(expectedId));

        verify(categoryGateway, times(1)).update(argThat(update ->
                Objects.equals(expectedName, update.getName())
                        && Objects.equals(expectedDescription, update.getDescription())
                        && Objects.equals(expectedIsActive, update.isActive())
                        && Objects.equals(expectedId, update.getId())
                        && Objects.equals(category.getCreatedAt(), update.getCreatedAt())
                        && category.getUpdatedAt().isBefore(update.getUpdatedAt())
                        && Objects.nonNull(update.getDeletedAt())));
    }

    @Test
    public void givenAnInvalidName_WhenCallsUpdateCategoryUseCase_ShouldReturnDomainException() {
        final var category = Category.newCategory("name", "desc", false);

        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;
        final var expectedId = category.getId();

        final var command = UpdateCategoryCommand
                .with(expectedId.getValue(), null, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        final var notification = useCase.execute(command).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.getErrors().get(0).message());

        verify(categoryGateway, times(0)).update(any());
    }

    @Test
    public void givenAValidCommand_WhenCallsUpdateCategoryUseCase_ShouldReturnCategoryId() {
        final var category = Category.newCategory("name", "desc", false);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = category.getId();

        final var command = UpdateCategoryCommand
                .with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        when(categoryGateway.update(any()))
                .thenAnswer(AdditionalAnswers.returnsFirstArg());

        final var output = useCase.execute(command).get();

        assertNotNull(output);
        assertNotNull(output.id());

        verify(categoryGateway, times(1)).findById(eq(expectedId));

        verify(categoryGateway, times(1)).update(argThat(update ->
                Objects.equals(expectedName, update.getName())
                        && Objects.equals(expectedDescription, update.getDescription())
                        && Objects.equals(expectedIsActive, update.isActive())
                        && Objects.equals(expectedId, update.getId())
                        && Objects.equals(category.getCreatedAt(), update.getCreatedAt())
                        && category.getUpdatedAt().isBefore(update.getUpdatedAt())
                        && Objects.isNull(update.getDeletedAt())));
    }

    @Test
    public void givenAValidCommand_WhenGatewayThrowsException_ShouldReturnException() {
        final var category = Category.newCategory("name", "desc", false);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Gateway error";
        final var expectedErrorCount = 1;
        final var expectedId = category.getId();

        final var command = UpdateCategoryCommand.with(expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(category.clone()));

        when(categoryGateway.update(any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var notification = useCase.execute(command).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.getErrors().get(0).message());

        verify(categoryGateway, times(1)).update(argThat(update ->
                Objects.equals(expectedName, update.getName())
                        && Objects.equals(expectedDescription, update.getDescription())
                        && Objects.equals(expectedIsActive, update.isActive())
                        && Objects.equals(expectedId, update.getId())
                        && Objects.equals(category.getCreatedAt(), update.getCreatedAt())
                        && category.getUpdatedAt().isBefore(update.getUpdatedAt())
                        && Objects.isNull(update.getDeletedAt())));
    }

    @Test
    public void givenCommandWithInvalidId_WhenCallsUpdateCategoryUseCase_ShouldReturnNotFound() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = "123";
        final var expectedErrorMessage = "Category with ID 123 was not found";
        final var expectedErrorCount = 1;

        final var command = UpdateCategoryCommand
                .with(expectedId, expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(eq(CategoryID.from(expectedId))))
                .thenReturn(Optional.empty());

        final var exception = assertThrows(NotFoundException.class, () -> useCase.execute(command));

        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(categoryGateway, times(1)).findById(eq(CategoryID.from(expectedId)));

        verify(categoryGateway, times(0)).update(any());
    }
}
