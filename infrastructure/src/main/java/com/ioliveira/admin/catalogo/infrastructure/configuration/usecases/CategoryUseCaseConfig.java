package com.ioliveira.admin.catalogo.infrastructure.configuration.usecases;

import com.ioliveira.admin.catalogo.application.category.create.CreateCategoryUseCase;
import com.ioliveira.admin.catalogo.application.category.create.DefaultCreateCategoryUseCase;
import com.ioliveira.admin.catalogo.application.category.delete.DefaultDeleteCategoryUseCase;
import com.ioliveira.admin.catalogo.application.category.delete.DeleteCategoryUseCase;
import com.ioliveira.admin.catalogo.application.category.retrieve.get.DefaultGetCategoryByIdUseCase;
import com.ioliveira.admin.catalogo.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.ioliveira.admin.catalogo.application.category.retrieve.list.DefaultListCategoriesUseCase;
import com.ioliveira.admin.catalogo.application.category.retrieve.list.ListCategoriesUseCase;
import com.ioliveira.admin.catalogo.application.category.update.DefaultUpdateCategoryUseCase;
import com.ioliveira.admin.catalogo.application.category.update.UpdateCategoryUseCase;
import com.ioliveira.admin.catalogo.domain.category.CategoryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class CategoryUseCaseConfig {

    private final CategoryGateway categoryGateway;

    public CategoryUseCaseConfig(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase() {
        return new DefaultCreateCategoryUseCase(categoryGateway);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase() {
        return new DefaultUpdateCategoryUseCase(categoryGateway);
    }

    @Bean
    public GetCategoryByIdUseCase getCategoryByIdUseCase() {
        return new DefaultGetCategoryByIdUseCase(categoryGateway);
    }

    @Bean
    public ListCategoriesUseCase listCategoriesUseCase() {
        return new DefaultListCategoriesUseCase(categoryGateway);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase() {
        return new DefaultDeleteCategoryUseCase(categoryGateway);
    }
}
