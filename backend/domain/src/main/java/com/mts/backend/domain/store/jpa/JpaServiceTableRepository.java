package com.mts.backend.domain.store.jpa;

import com.mts.backend.domain.store.ServiceTableEntity;
import com.mts.backend.domain.store.value_object.TableNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
@Repository
public interface JpaServiceTableRepository extends JpaRepository<ServiceTableEntity, Integer> {
  @EntityGraph(attributePaths = {"areaEntity"}, type = EntityGraph.EntityGraphType.FETCH)
  @Query("select s from ServiceTableEntity s WHERE s.active = :active")
  Slice<ServiceTableEntity> findAllByActiveFetchArea(@Param("active") @NonNull Boolean active, Pageable pageable);

  @EntityGraph(attributePaths = {"areaEntity"}, type = EntityGraph.EntityGraphType.FETCH)
  @Query("select s from ServiceTableEntity s WHERE s.active = :active")
  List<ServiceTableEntity> findAllByActiveFetchArea(@Param("active") @NonNull Boolean active);
  
  
  @EntityGraph(attributePaths = {"areaEntity"})
  @Query("select s from ServiceTableEntity s WHERE (s.active = :active or :active is null)")
  Page<ServiceTableEntity> findAllFetchArea(@Param("active") @Nullable Boolean active, Pageable pageable);

  @EntityGraph(attributePaths = {"areaEntity"})
  @Query("select s from ServiceTableEntity s")
  Page<ServiceTableEntity> findAllFetchArea(Pageable pageable);


  @Query("select count(s) from ServiceTableEntity s where s.areaEntity.id = :id")
  long countByAreaEntity_Id(@Param("id") Integer id);

  @Query("select s from ServiceTableEntity s where s.tableNumber = :tableNumber")
  Optional<ServiceTableEntity> findByTableNumber(@Param("tableNumber") @NonNull TableNumber tableNumber);

  @Query("select (count(s) > 0) from ServiceTableEntity s where s.tableNumber = :tableNumber")
  boolean existsByTableNumber(@Param("tableNumber") @NonNull TableNumber tableNumber);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM milk_tea_shop_prod.ServiceTable WHERE table_id = :id", nativeQuery = true)
  void deleteServiceTable(@Param("id") Integer id);

  @Query("select s from ServiceTableEntity s where s.areaEntity.id = :id")
  Set<ServiceTableEntity> findByAreaEntity_Id(@Param("id") @NonNull Integer id);


  
  @EntityGraph(attributePaths = {"areaEntity"})
    @Query("select s from ServiceTableEntity s where s.id = :id")
  Optional<ServiceTableEntity> findByIdWithArea(Integer serviceTableId);

  @Query("select (count(s) > 0) from ServiceTableEntity s where s.id <> :id and s.tableNumber = :tableNumber")
  boolean existsByIdNotAndTableNumber(@Param("id") @NonNull Integer id, @Param("tableNumber") @NonNull TableNumber tableNumber);

}