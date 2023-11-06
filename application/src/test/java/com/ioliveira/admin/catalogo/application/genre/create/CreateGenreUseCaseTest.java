package com.ioliveira.admin.catalogo.application.genre.create;

import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.category.CategoryID;
import com.ioliveira.admin.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateGenreUseCaseTest {

    @InjectMocks
    private DefaultCreateGenreUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @BeforeEach
    void cleanUp() {
        reset(categoryGateway);
        reset(genreGateway);
    }

    @Test
    public void givenAValidCommand_WhenCallsCreateGenreUseCase_ShouldReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, categoryIdsAsString(expectedCategories));

        when(genreGateway.create(any()))
                .thenAnswer(AdditionalAnswers.returnsFirstArg());

        final var output = useCase.execute(command);

        assertNotNull(output);
        assertNotNull(output.id());

        verify(genreGateway, times(1)).create(argThat(genre ->
                Objects.equals(expectedName, genre.getName())
                        && Objects.equals(expectedIsActive, genre.isActive())
                        && Objects.equals(expectedCategories, genre.getCategories())
                        && Objects.nonNull(genre.getId())
                        && Objects.nonNull(genre.getCreatedAt())
                        && Objects.nonNull(genre.getUpdatedAt())
                        && Objects.isNull(genre.getDeletedAt())));
    }

    private List<String> categoryIdsAsString(final List<CategoryID> expectedCategories) {
        return expectedCategories.stream().map(CategoryID::getValue).toList();
    }

}
