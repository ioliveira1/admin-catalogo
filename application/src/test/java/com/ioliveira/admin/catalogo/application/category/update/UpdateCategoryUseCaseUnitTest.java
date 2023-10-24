package com.ioliveira.admin.catalogo.application.category.update;

import com.ioliveira.admin.catalogo.domain.category.Category;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryUseCaseUnitTest {

    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

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
                .thenReturn(Optional.of(category));

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
                        && category.getUpdatedAt().isBefore(update.getUpdatedAt()) //TODO: instancia retornada pelo update é a mesma da criada em Category.newCategory()... Datas ão iguais
                        && Objects.isNull(update.getDeletedAt())));
    }
}
