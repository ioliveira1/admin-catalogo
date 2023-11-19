package com.ioliveira.admin.catalogo;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test-integration")
@DataJpaTest
//@ComponentScan("com.ioliveira.admin.catalogo")
@Tag("integrationTest")
@ComponentScan(
        basePackages = "com.ioliveira.admin.catalogo",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".[MySQLGateway]")
        })
public @interface MySQLGatewayTest {
}
