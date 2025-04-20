package com.mts.backend.domain.product.jpa;

import com.mts.backend.domain.product.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCategoryRepository extends JpaRepository<Category, Integer> {
    @Query("select (count(c) > 0) from Category c where c.name = :name")
    boolean existsByName(@Param("name") @NonNull String name);

    @Query("select c from Category c where c.name = :name")
    Optional<Category> findByName(@Param("name") @NonNull String name);
    

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Category WHERE category_id = :id", nativeQuery = true)
    void deleteCategory(@Param("id") @NonNull Integer id);
    
    @EntityGraph(attributePaths = {"products"})
    @Query("select c from Category c where c.id = :id")
    Optional<Category> findByIdFetch(@Param("id") @NonNull Integer id);
}