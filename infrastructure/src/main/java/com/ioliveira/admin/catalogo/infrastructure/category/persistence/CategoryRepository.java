package com.ioliveira.admin.catalogo.infrastructure.category.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryJpaEntity, String> {

    Page<CategoryJpaEntity> findAll(Specification<CategoryJpaEntity> whereClause, Pageable page);

    /*@Query(value = "select c.id from CategoryJpaEntity c where c.id in :ids")
    List<String> existsById(@Param("ids") List<String> ids);*/
    List<CategoryJpaEntity> findByIdIn(List<String> ids);
}
