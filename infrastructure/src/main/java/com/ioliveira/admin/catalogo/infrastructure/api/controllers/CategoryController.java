package com.ioliveira.admin.catalogo.infrastructure.api.controllers;

import com.ioliveira.admin.catalogo.application.category.create.CreateCategoryUseCase;
import com.ioliveira.admin.catalogo.domain.pagination.Pagination;
import com.ioliveira.admin.catalogo.infrastructure.api.CategoryAPI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;

    public CategoryController(final CreateCategoryUseCase createCategoryUseCase) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
    }

    @Override
    public ResponseEntity<?> createCategory() {
        return null;
    }

    @Override
    public Pagination<?> listCategories(final String search, final int page, final int perPage, final String sort, final String direction) {
        return null;
    }
}
