package com.mts.backend.domain.product.jpa;

import com.mts.backend.domain.product.CategoryEntity;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.value_object.CategoryName;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    @Query("select (count(c) > 0) from CategoryEntity c where c.name = :name")
    boolean existsByName(@Param("name") @NonNull CategoryName name);

    @Query("select c from CategoryEntity c where c.name = :name")
    Optional<CategoryEntity> findByName(@Param("name") @NonNull CategoryName name);

    @EntityGraph(attributePaths = {"parentCategoryEntity"})
    @Query("select c from CategoryEntity c")
    List<CategoryEntity> findAllWithJoinFetch(Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Category WHERE category_id = :id", nativeQuery = true)
    void deleteCategory(@Param("id") @NonNull Integer id);
}