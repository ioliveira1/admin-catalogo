package com.ioliveira.admin.catalogo.application;

import com.ioliveira.admin.catalogo.domain.Category;

public class UseCase {
    public Category execute() {
        return new Category();
    }
}