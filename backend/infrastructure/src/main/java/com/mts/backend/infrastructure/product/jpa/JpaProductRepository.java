package com.mts.backend.infrastructure.product.jpa;

import com.mts.backend.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, Integer> {
    @Query("select p from ProductEntity p where upper(p.name) = upper(?1)")
    Optional<ProductEntity> findByName(@NonNull String name);

    @Query("select p from ProductEntity p where p.isAvailable = ?1")
    List<ProductEntity> findByIsAvailable(boolean isAvailable);

    @Query("select p from ProductEntity p where p.isSignature = ?1")
    List<ProductEntity> findByIsSignature(@NonNull boolean isSignature);

    @Query("select (count(p) > 0) from ProductEntity p where p.name = ?1")
    boolean existsByName(@NonNull String name);
    
}