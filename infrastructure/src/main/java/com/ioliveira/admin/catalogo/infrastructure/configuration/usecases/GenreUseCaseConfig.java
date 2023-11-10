package com.ioliveira.admin.catalogo.infrastructure.configuration.usecases;

import com.ioliveira.admin.catalogo.application.genre.create.CreateGenreUseCase;
import com.ioliveira.admin.catalogo.application.genre.create.DefaultCreateGenreUseCase;
import com.ioliveira.admin.catalogo.application.genre.delete.DefaultDeleteGenreUseCase;
import com.ioliveira.admin.catalogo.application.genre.delete.DeleteGenreUseCase;
import com.ioliveira.admin.catalogo.application.genre.retreieve.get.DefaultGetGenreByIdUseCase;
import com.ioliveira.admin.catalogo.application.genre.retreieve.get.GetGenreByIdUseCase;
import com.ioliveira.admin.catalogo.application.genre.retreieve.list.DefaultListGenreUseCase;
import com.ioliveira.admin.catalogo.application.genre.retreieve.list.ListGenreUseCase;
import com.ioliveira.admin.catalogo.application.genre.update.DefaultUpdateGenreUseCase;
import com.ioliveira.admin.catalogo.application.genre.update.UpdateGenreUseCase;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import com.ioliveira.admin.catalogo.domain.genre.GenreGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class GenreUseCaseConfig {

    private final GenreGateway genreGateway;
    private final CategoryGateway categoryGateway;

    public GenreUseCaseConfig(final GenreGateway genreGateway, final CategoryGateway categoryGateway) {
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Bean
    public CreateGenreUseCase createGenreUseCase() {
        return new DefaultCreateGenreUseCase(genreGateway, categoryGateway);
    }

    @Bean
    public UpdateGenreUseCase updateGenreUseCase() {
        return new DefaultUpdateGenreUseCase(genreGateway, categoryGateway);
    }

    @Bean
    public GetGenreByIdUseCase getGenreByIdUseCase() {
        return new DefaultGetGenreByIdUseCase(genreGateway);
    }

    @Bean
    public ListGenreUseCase listGenreUseCase() {
        return new DefaultListGenreUseCase(genreGateway);
    }

    @Bean
    public DeleteGenreUseCase deleteGenreUseCase() {
        return new DefaultDeleteGenreUseCase(genreGateway);
    }
}
