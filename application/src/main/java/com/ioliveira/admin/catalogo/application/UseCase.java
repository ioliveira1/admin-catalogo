package com.ioliveira.admin.catalogo.application;

import com.ioliveira.admin.catalogo.domain.category.Category;

public class UseCase {
    public Category execute() {
        return new Category();
    }
}