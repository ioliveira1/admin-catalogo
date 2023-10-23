package com.ioliveira.admin.catalogo.application.category.create;

import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateCategoryUseCaseUnitTest {

    @Test
    public void givenAValidCommand_WhenCallsCreateCategoryUseCase_ShouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var command = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

        final CategoryGateway categoryGateway = mock(CategoryGateway.class);

        when(categoryGateway.create(any()))
                .thenAnswer(AdditionalAnswers.returnsFirstArg());

        final var useCase = new DefaultCreateCategoryUseCase(categoryGateway);

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(categoryGateway, times(1)).create(argThat(category ->
                Objects.equals(expectedName, category.getName())
                        && Objects.equals(expectedDescription, category.getDescription())
                        && Objects.equals(expectedIsActive, category.isActive())
                        && Objects.nonNull(category.getId())
                        && Objects.nonNull(category.getCreatedAt())
                        && Objects.nonNull(category.getUpdatedAt())
                        && Objects.isNull(category.getDeletedAt())));
    }

}
