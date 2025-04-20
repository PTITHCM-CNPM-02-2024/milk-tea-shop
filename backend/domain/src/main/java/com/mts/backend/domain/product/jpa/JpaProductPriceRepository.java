package com.mts.backend.domain.product.jpa;

import com.mts.backend.domain.product.ProductPrice;
import com.mts.backend.domain.product.identifier.ProductId;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    @Query("select (count(p) > 0) from ProductPrice p where p.product.id = :id and p.size.id = :id1")
    boolean existsByProductEntity_IdAndSize_Id(@Param("ids") @NonNull Integer id,
                                              @Param("id1") @NonNull Integer id1);

    @Query("select (count(p) > 0) from ProductPrice p where (p.product.id, p.size.id) in " +
            "(select p2.product.id, p2.size.id from ProductPrice p2 where p2.product.id in :productIds and p2.size.id in :sizeIds)")
    boolean existsByProductEntity_IdInAndSize_IdIn(@Param("productIds") @NonNull Collection<Integer> productIds,
                                                  @Param("sizeIds") @NonNull Collection<Integer> sizeIds);

    @Query("select p from ProductPrice p where p.product.id = ?1")
    Set<ProductPrice> findPricesByProductId(@NonNull ProductId id);

    @EntityGraph(attributePaths = {"product", "size"})
    @Query("select p from ProductPrice p where p.product.id = :id and p.size.id = :id1")
    Optional<ProductPrice> findByProductEntity_IdAndSize_Id(@Param("id") @NonNull Integer id,
                                                            @Param("id1") @NonNull Integer id1);

    @EntityGraph(attributePaths = {"product.productPrices", "size"})
    @Query("select p from ProductPrice p where p.product.id = :id and p.size.id = :id1")
    Optional<ProductPrice> findByProductEntity_IdAndSize_IdFetchPrices(@Param("id") @NonNull Integer id,
                                                                       @Param("id1") @NonNull Integer id1);    
    
}