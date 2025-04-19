package com.mts.backend.domain.store.jpa;


import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import com.mts.backend.domain.store.Area;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface JpaAreaRepository extends JpaRepository<Area, Integer> {
    @Query("select a from Area a where a.active = true")
    List<Area> findByActiveTrue();

    @Query("select a from Area a where a.active = :active")
    Slice<Area> findByActive(@Param("active") @NonNull Boolean active, Pageable pageable);

    @Query("select a from Area a where a.active = :active")
    List<Area> findByActive(@Param("active") @NonNull Boolean active);

    @Query("select a from Area a where a.createdAt is null or a.createdAt > :createdAt")
    List<Area> findByCreatedAtNullOrCreatedAtGreaterThan(@Param("createdAt") @NonNull LocalDateTime createdAt, Pageable pageable);

    @Query("select a from Area a where a.name = :name")
    Optional<Area> findByName(@Param("name") @NonNull String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.AreaEntity WHERE area_id = :id", nativeQuery = true)
    void deleteArea(@Param("id") Integer id);

    @Query("select (count(a) > 0) from Area a where a.name = :name")
    boolean existsByName(@Param("name") @NonNull String name);

    @Query("select (count(a) > 0) from Area a where a.id <> :id and a.name = :name")
    boolean existsByIdNotAndName(@Param("id") Integer id, @Param("name") @NonNull String name);
    
    @EntityGraph(attributePaths = {"serviceTables"})
    @Query("select a from Area a where a.id = :id")
    Optional<Area> findByIdFetch(@Param("id") @NonNull Integer id);
    
    @Query("""
            select a from Area a
            where a.active = :active
            """)
    Slice<Area> findAllByActiveNotFetch(@Param("active") @NonNull Boolean active, Pageable pageable);
}