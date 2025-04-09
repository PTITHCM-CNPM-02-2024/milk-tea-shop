package com.mts.backend.domain.store.jpa;

import com.mts.backend.domain.store.AreaEntity;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.value_object.AreaName;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
@Repository
public interface JpaAreaRepository extends JpaRepository<AreaEntity, Integer> {
    @Query("select a from AreaEntity a where a.active = true")
    List<AreaEntity> findByActiveTrue();

    @Query("select a from AreaEntity a where a.active = :active")
    Slice<AreaEntity> findByActive(@Param("active") @NonNull Boolean active, Pageable pageable);

    @Query("select a from AreaEntity a where a.createdAt is null or a.createdAt > :createdAt")
    List<AreaEntity> findByCreatedAtNullOrCreatedAtGreaterThan(@Param("createdAt") @NonNull LocalDateTime createdAt, Pageable pageable);

    @Query("select a from AreaEntity a where a.name = :name")
    Optional<AreaEntity> findByName(@Param("name") @NonNull AreaName name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Area WHERE area_id = :id", nativeQuery = true)
    void deleteArea(@Param("id") Integer id);

    @Query("select (count(a) > 0) from AreaEntity a where a.name = :name")
    boolean existsByName(@Param("name") @NonNull AreaName name);

    @Query("select (count(a) > 0) from AreaEntity a where a.id <> :id and a.name = :name")
    boolean existsByIdNotAndName(@Param("id") Integer id, @Param("name") @NonNull AreaName name);
    
    @EntityGraph(attributePaths = {"serviceTables"})
    @Query("select a from AreaEntity a where a.id = :id")
    Optional<AreaEntity> findByIdFetch(@Param("id") @NonNull Integer id);
    
    @Query("""
            select a from AreaEntity a
            where a.active = :active
            """)
    Slice<AreaEntity> findAllByActiveNotFetch(@Param("active") @NonNull Boolean active, Pageable pageable);
}