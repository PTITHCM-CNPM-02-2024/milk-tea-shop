package com.mts.backend.domain.product.jpa;

import com.mts.backend.domain.product.ProductEntity;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.value_object.ProductName;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, Integer> {
    @Query("select (count(p) > 0) from ProductEntity p where p.id in :ids")
    boolean existsByIdIn(@Param("ids") @NonNull Collection<ProductId> ids);

    @Query(value = "SELECT * FROM milk_tea_shop_prod.Product p WHERE UPPER(p.name) = UPPER(:#{#name})", nativeQuery = true)
    Optional<ProductEntity> findByName(@Param("name") @NonNull ProductName name);

    @Query("select p from ProductEntity p where p.signature = :signature or p.available = :available")
    List<ProductEntity> findBySignatureOrAvailable(@Param("signature") @NonNull Boolean signature, @Param("available") @NonNull Boolean available, Pageable pageable);

    @Query("select (count(p) > 0) from ProductEntity p where p.name = :name")
    boolean existsByName(@Param("name") @NonNull ProductName name);


    @Query("select p from ProductEntity p where p.signature = :signature")
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices.size.unit"})
    List<ProductEntity> findBySignature(@Param("signature") @NonNull Boolean signature, Pageable pageable);

    @Query("select p from ProductEntity p where p.id in (:ids)")
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices.size.unit"})
    List<ProductEntity> findAllByIdIn(@Param("ids") List<ProductId> ids);

    @Query("select p from ProductEntity p")
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices.size.unit"})
    List<ProductEntity> findAllWithDetails();

    @Query("select p from ProductEntity p")
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices.size.unit"})
    List<ProductEntity> findAllWithDetails(Pageable pageable);
    
    @Query("select p from ProductEntity p where p.id in (:ids) and p.available = :available")
    @EntityGraph(attributePaths = {"categoryEntity", "productPrices"})
    Set<ProductEntity> findAllByIdInAndAvailable(@Param("ids") List<ProductId> ids,
                                                 @Param("available") Boolean available);

    @EntityGraph(attributePaths = {"productPrices"})
    @Query("select p from ProductEntity p where p.id = :id")
    Optional<ProductEntity> findByIdWithDetails(@Param("id") @NonNull Integer id);

    @Query("select (count(p) > 0) from ProductEntity p where p.id <> :id and p.name = :name")
    boolean existsByIdNotAndName(@Param("id") @NonNull Integer id, @Param("name") @NonNull ProductName name);
}