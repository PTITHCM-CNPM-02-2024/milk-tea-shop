package com.mts.backend.domain.product.jpa;

import com.mts.backend.domain.product.Product;
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
public interface JpaProductRepository extends JpaRepository<Product, Integer> {
    @Query("select (count(p) > 0) from Product p where p.id in :ids")
    boolean existsByIdIn(@Param("ids") @NonNull Collection<Integer> ids);
    

    @Query("select p from Product p where p.signature = :signature or p.available = :available")
    List<Product> findBySignatureOrAvailable(@Param("signature") @NonNull Boolean signature, @Param("available") @NonNull Boolean available, Pageable pageable);

    @Query("select (count(p) > 0) from Product p where p.name = :name")
    boolean existsByName(@Param("name") @NonNull ProductName name);


    @Query("select p from Product p where p.signature = :signature and p.available = :available and size(p.productPrices) > 0")
    @EntityGraph(attributePaths = {"category", "productPrices.size.unit"})
    Page<Product> findBySignature(@Param("signature") @NonNull Boolean signature, Pageable pageable);

    @Query("select p from Product p where p.id in (:ids)")
    @EntityGraph(attributePaths = {"category", "productPrices.size.unit"})
    List<Product> findAllByIdIn(@Param("ids") List<ProductId> ids);

    @Query("""
            select p from Product p
            where (p.category.id <> 1 or p.category is null)
            and (
            (:isOrdered = true and size(p.productPrices) > 0 and p.available = true)
            or
            (:isOrdered = false and (size(p.productPrices) = 0 or p.available = false)))
            """)
    @EntityGraph(attributePaths = {"category", "productPrices"})
    List<Product> findAllForSaleFetch(@Param("isOrdered") Boolean isOrdered);

    @Query("""
            select p from Product p
            where (p.category.id <> 1 or p.category is null)
            and (
            (:isOrdered = true and size(p.productPrices) > 0 and p.available = true)
            or
            (:isOrdered = false and (size(p.productPrices) = 0 or p.available = false)))
            """)
    @EntityGraph(attributePaths = {"category", "productPrices.size.unit"})
    Slice<Product> findAllForSaleFetch(@Param("isOrdered") Boolean isOrdered, Pageable pageable);
    
    @Query("select p from Product p")
    @EntityGraph(attributePaths = {"category", "productPrices.size.unit"})
    Page<Product> findAllFetch(Pageable pageable);
    
    @Query("select p from Product p where p.id in (:ids) and p.available = :available")
    @EntityGraph(attributePaths = {"category", "productPrices"})
    Set<Product> findAllByIdInAndAvailable(@Param("ids") List<ProductId> ids,
                                           @Param("available") Boolean available);

    @EntityGraph(attributePaths = {"category", "productPrices.size.unit"})
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdFetch(@Param("id") @NonNull Integer id);

    @Query("select (count(p) > 0) from Product p where p.id <> :id and p.name = :name")
    boolean existsByIdNotAndName(@Param("id") @NonNull Integer id, @Param("name") @NonNull ProductName name);
    
    @Query("select p from Product p where p.category.id = ?1")
    @EntityGraph(attributePaths = {"category", "productPrices.size.unit"})
    List<Product> findAllByCategoryEntity_Id(@NotNull Integer categoryId);


    @Query("""
        select p from Product p
        where p.category.id = :catId
        and (:isOrdered = true and size(p.productPrices) > 0 and p.available = true)
        or (:isOrdered = false and (size(p.productPrices) = 0 or p.available = false) or (:isOrdered = null))
        """)
    @EntityGraph(attributePaths = {"category", "productPrices"})
    Slice<Product> findAllByCatIdAndOrderedFetch(@Param("catId") @Nullable Integer catId,
                                                 @Param("isOrdered") @Nullable Boolean isOrdered,
                                                 Pageable pageable);
    
    
}