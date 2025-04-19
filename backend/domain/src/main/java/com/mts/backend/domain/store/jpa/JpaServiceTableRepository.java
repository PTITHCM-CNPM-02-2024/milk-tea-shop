package com.mts.backend.domain.store.jpa;

import com.mts.backend.domain.store.ServiceTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaServiceTableRepository extends JpaRepository<ServiceTable, Integer> {
  @EntityGraph(attributePaths = {"areaEntity"}, type = EntityGraph.EntityGraphType.FETCH)
  @Query("select s from ServiceTable s WHERE s.active = :active")
  Slice<ServiceTable> findAllByActiveFetchArea(@Param("active") @NonNull Boolean active, Pageable pageable);

  @EntityGraph(attributePaths = {"areaEntity"}, type = EntityGraph.EntityGraphType.FETCH)
  @Query("select s from ServiceTable s WHERE s.active = :active")
  List<ServiceTable> findAllByActiveFetchArea(@Param("active") @NonNull Boolean active);
  
  
  @EntityGraph(attributePaths = {"areaEntity"})
  @Query("select s from ServiceTable s WHERE (s.active = :active or :active is null)")
  Page<ServiceTable> findAllFetchArea(@Param("active") @Nullable Boolean active, Pageable pageable);

  @EntityGraph(attributePaths = {"areaEntity"})
  @Query("select s from ServiceTable s")
  Page<ServiceTable> findAllFetchArea(Pageable pageable);


  @Query("select count(s) from ServiceTable s where s.area.id = :id")
  long countByAreaEntity_Id(@Param("id") Integer id);

  @Query("select s from ServiceTable s where s.tableNumber = :tableNumber")
  Optional<ServiceTable> findByTableNumber(@Param("tableNumber") @NonNull String tableNumber);

  @Query("select (count(s) > 0) from ServiceTable s where s.tableNumber = :tableNumber")
  boolean existsByTableNumber(@Param("tableNumber") @NonNull String tableNumber);
  

  @Query("select s from ServiceTable s where s.area.id = :id")
  Page<ServiceTable> findByAreaEntity_Id(@Param("id") @Nullable Integer id, Pageable pageable);


  
  @EntityGraph(attributePaths = {"areaEntity"})
    @Query("select s from ServiceTable s where s.id = :id")
  Optional<ServiceTable> findByIdWithArea(@Param("id")  @NonNull Integer id);

  @Query("select (count(s) > 0) from ServiceTable s where s.id <> :id and s.tableNumber = :tableNumber")
  boolean existsByIdNotAndTableNumber(@Param("id") @NonNull Integer id, @Param("tableNumber") @NonNull String tableNumber);

}