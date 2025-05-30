package com.mts.backend.domain.product.jpa;

import com.mts.backend.domain.product.ProductSizeEntity;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
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
public interface JpaProductSizeRepository extends JpaRepository<ProductSizeEntity, Integer> {
    @Query("select p from ProductSizeEntity p where p.name = :name")
    Optional<ProductSizeEntity> findByName(@NonNull ProductSizeName name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.ProductSize WHERE size_id = :id", nativeQuery = true)
    void deleteProductSize(@Param("id") @NonNull Integer id);

    @Query("select (count(p) > 0) from ProductSizeEntity p where p.name = :name and p.unit.id = :id")
    boolean existsByNameAndUnit_Id(@Param("name") @NonNull ProductSizeName name, @Param("id") @NonNull Integer id);

    @Query("select (count(p) > 0) from ProductSizeEntity p where p.id <> :id and p.unit.id = :id1 and p.name = :name")
    boolean existsByIdNotAndUnit_IdAndName(@Param("id") @NonNull Integer id, @Param("id1") @NonNull Integer id1, @Param("name") @NonNull ProductSizeName name);


    @Query("select p from ProductSizeEntity p where p.name = ?1 and p.unit.id = ?2")
    Optional<ProductSizeEntity> findByNameAndUnit(@NonNull ProductSizeName name, @NonNull Integer id);


    @Query("select p from ProductSizeEntity p")
    @EntityGraph(value = "ProductSizeEntity.unit", type = EntityGraph.EntityGraphType.FETCH)
    List<ProductSizeEntity> findAllWithJoinFetch();

    @Query("select p from ProductSizeEntity p")
    @EntityGraph(value = "ProductSizeEntity.unit", type = EntityGraph.EntityGraphType.FETCH)
    Page<ProductSizeEntity> findAllWithJoinFetch(Pageable pageable);
}
