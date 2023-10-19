package com.ioliveira.admin.catalogo.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CategoryUnitTest {

    @Test
    public void testNewCategory() {
        Assertions.assertNotNull(new Category());
    }
}
