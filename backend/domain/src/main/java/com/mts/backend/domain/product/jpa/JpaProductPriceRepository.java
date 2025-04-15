package com.mts.backend.domain.product.jpa;

import com.mts.backend.domain.product.ProductPriceEntity;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaProductPriceRepository extends JpaRepository<ProductPriceEntity, Long> {
    @Query("select (count(p) > 0) from ProductPriceEntity p where p.productEntity.id = :id and p.size.id = :id1")
    boolean existsByProductEntity_IdAndSize_Id(@Param("ids") @NonNull Integer id,
                                              @Param("id1") @NonNull Integer id1);

    @Query("select (count(p) > 0) from ProductPriceEntity p where (p.productEntity.id, p.size.id) in " +
            "(select p2.productEntity.id, p2.size.id from ProductPriceEntity p2 where p2.productEntity.id in :productIds and p2.size.id in :sizeIds)")
    boolean existsByProductEntity_IdInAndSize_IdIn(@Param("productIds") @NonNull Collection<Integer> productIds,
                                                  @Param("sizeIds") @NonNull Collection<Integer> sizeIds);

    @Query("select p from ProductPriceEntity p where p.productEntity.id = ?1")
    Set<ProductPriceEntity> findPricesByProductId(@NonNull ProductId id);

    @EntityGraph(attributePaths = {"productEntity", "size"})
    @Query("select p from ProductPriceEntity p where p.productEntity.id = :id and p.size.id = :id1")
    Optional<ProductPriceEntity> findByProductEntity_IdAndSize_Id(@Param("id") @NonNull Integer id,
                                                                  @Param("id1") @NonNull Integer id1);

    @EntityGraph(attributePaths = {"productEntity.productPrices", "size"})
    @Query("select p from ProductPriceEntity p where p.productEntity.id = :id and p.size.id = :id1")
    Optional<ProductPriceEntity> findByProductEntity_IdAndSize_IdFetchPrices(@Param("id") @NonNull Integer id,
                                                                  @Param("id1") @NonNull Integer id1);    
    
}