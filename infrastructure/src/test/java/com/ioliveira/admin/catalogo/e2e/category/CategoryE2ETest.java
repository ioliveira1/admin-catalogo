package com.ioliveira.admin.catalogo.e2e.category;

import com.ioliveira.admin.catalogo.E2ETest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@E2ETest
@Testcontainers
public class CategoryE2ETest {
    public static final int ORIGINAL_PORT = 3306;

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER =
            new MySQLContainer<>(DockerImageName.parse("mysql:8.2.0"))
                    .withPassword("123456")
                    .withUsername("root")
                    .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add(
                "mysql.port",
                () -> MYSQL_CONTAINER.getMappedPort(ORIGINAL_PORT)
        );
    }

    @Test
    public void test() {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
    }

}
