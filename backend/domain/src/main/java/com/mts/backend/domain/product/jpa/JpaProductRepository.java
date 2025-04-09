package com.mts.backend.domain.product.jpa;

import com.mts.backend.domain.product.ProductEntity;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.value_object.ProductName;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, Integer> {
    @Query("select (count(p) > 0) from ProductEntity p where p.id in :ids")
    boolean existsByIdIn(@Param("ids") @NonNull Collection<Integer> ids);
    

    @Query("select p from ProductEntity p where p.signature = :signature or p.available = :available")
    List<ProductEntity> findBySignatureOrAvailable(@Param("signature") @NonNull Boolean signature, @Param("available") @NonNull Boolean available, Pageable pageable);

    @Query("select (count(p) > 0) from ProductEntity p where p.name = :name")
    boolean existsByName(@Param("name") @NonNull ProductName name);


    @Query("select p from ProductEntity p where p.signature = :signature and p.available = :available and size(p.productPrices) > 0")
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices.size.unit"})
    Page<ProductEntity> findBySignature(@Param("signature") @NonNull Boolean signature, Pageable pageable);

    @Query("select p from ProductEntity p where p.id in (:ids)")
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices.size.unit"})
    List<ProductEntity> findAllByIdIn(@Param("ids") List<ProductId> ids);

    @Query("""
            select p from ProductEntity p
            where (p.categoryEntity.id <> 1 or p.categoryEntity is null)
            and (
            (:isOrdered = true and size(p.productPrices) > 0 and p.available = true)
            or
            (:isOrdered = false and (size(p.productPrices) = 0 or p.available = false)))
            """)
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices.size.unit"})
    List<ProductEntity> findAllForSaleFetch(@Param("isOrdered") Boolean isOrdered);

    @Query("""
            select p from ProductEntity p
            where (p.categoryEntity.id <> 1 or p.categoryEntity is null)
            and (
            (:isOrdered = true and size(p.productPrices) > 0 and p.available = true)
            or
            (:isOrdered = false and (size(p.productPrices) = 0 or p.available = false)))
            """)
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices.size.unit"})
    Slice<ProductEntity> findAllForSaleFetch(@Param("isOrdered") Boolean isOrdered, Pageable pageable);
    
    @Query("select p from ProductEntity p")
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices.size.unit"})
    Page<ProductEntity> findAllFetch(Pageable pageable);
    
    @Query("select p from ProductEntity p where p.id in (:ids) and p.available = :available")
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices"})
    Set<ProductEntity> findAllByIdInAndAvailable(@Param("ids") List<ProductId> ids,
                                                 @Param("available") Boolean available);

    @EntityGraph(attributePaths = {"categoryEntity", "productPrices.size.unit"})
    @Query("select p from ProductEntity p where p.id = :id")
    Optional<ProductEntity> findByIdFetch(@Param("id") @NonNull Integer id);

    @Query("select (count(p) > 0) from ProductEntity p where p.id <> :id and p.name = :name")
    boolean existsByIdNotAndName(@Param("id") @NonNull Integer id, @Param("name") @NonNull ProductName name);
    
    @Query("select p from ProductEntity p where p.categoryEntity.id = ?1")
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices.size.unit"})
    List<ProductEntity> findAllByCategoryEntity_Id(@NotNull Integer categoryEntityId);


    @Query("""
        select p from ProductEntity p
        where p.categoryEntity.id = :catId
        and (:isOrdered = true and size(p.productPrices) > 0 and p.available = true)
        or (:isOrdered = false and (size(p.productPrices) = 0 or p.available = false) or (:isOrdered = null))
        """)
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices"})
    Slice<ProductEntity> findAllByCatIdAndOrderedFetch(@Param("catId") @Nullable Integer catId,
                                                       @Param("isOrdered") @Nullable Boolean isOrdered,
                                                       Pageable pageable);
    
    
}