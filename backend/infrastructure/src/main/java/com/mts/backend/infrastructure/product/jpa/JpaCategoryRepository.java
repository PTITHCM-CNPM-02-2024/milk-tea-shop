package com.mts.backend.infrastructure.product.jpa;

import com.mts.backend.infrastructure.persistence.entity.CategoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    @Query("select c from CategoryEntity c where upper(c.name) = upper(?1)")
    Optional<CategoryEntity> findByName(@NonNull String name);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.Category (category_id, name, description, parent_category_id) " +
            "VALUES (:#{#entity.id}, :#{#entity.name}, :#{#entity.description}, :#{#entity.parentCategoryEntity?.id})", nativeQuery = true)
    void insertCategory(@Param("entity") @NonNull CategoryEntity entity);

    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.Category SET " +
            "name = :#{#entity.name}, " +
            "description = :#{#entity.description}, " +
            "parent_category_id = :#{#entity.parentCategoryEntity?.id} " +
            "WHERE category_id = :#{#entity.id}", nativeQuery = true)
    void updateCategory(@Param("entity") @NonNull CategoryEntity entity);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Category WHERE category_id = :id", nativeQuery = true)
    void deleteCategory(@Param("id") @NonNull Integer id);
}