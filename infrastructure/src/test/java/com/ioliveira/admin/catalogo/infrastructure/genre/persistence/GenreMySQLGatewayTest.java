package com.ioliveira.admin.catalogo.infrastructure.genre.persistence;

import com.ioliveira.admin.catalogo.MySQLGatewayTest;
import com.ioliveira.admin.catalogo.infrastructure.category.CategoryMySQLGateway;
import com.ioliveira.admin.catalogo.infrastructure.genre.GenreMySQLGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@MySQLGatewayTest
public class GenreMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryMySQLGateway;

    @Autowired
    private GenreMySQLGateway genreMySQLGateway;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void cleanUp() {
        this.genreRepository.deleteAll();
    }

    @Test
    public void test() {
        Assertions.assertNotNull(categoryMySQLGateway);
        Assertions.assertNotNull(genreMySQLGateway);
        Assertions.assertNotNull(genreRepository);
    }

}
