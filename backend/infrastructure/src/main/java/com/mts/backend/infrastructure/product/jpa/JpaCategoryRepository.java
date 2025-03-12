package com.mts.backend.infrastructure.product.jpa;

import com.mts.backend.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    @Query("select c from CategoryEntity c where upper(c.name) = upper(?1)")
    Optional<CategoryEntity> findByName(@NonNull String name);

}