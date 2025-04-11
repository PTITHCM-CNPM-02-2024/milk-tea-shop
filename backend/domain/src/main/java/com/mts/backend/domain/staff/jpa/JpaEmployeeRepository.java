package com.mts.backend.domain.staff.jpa;

import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.staff.EmployeeEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaEmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    @Query("select (count(e) > 0) from EmployeeEntity e where e.accountEntity.id = :id")
    boolean existsByAccountId(@Param("id") @NonNull Long id);

    @Query("select (count(e) > 0) from EmployeeEntity e where e.email = :email")
    boolean existsByEmail(@Param("email") @NonNull Email email);

    @Query("select (count(e) > 0) from EmployeeEntity e where e.id <> :id and e.email = :email")
    boolean existsByIdNotAndEmail(@Param("id") @NonNull Long id, @Param("email") @NonNull Email email);
    @Query("select (count(e) > 0) from EmployeeEntity e where e.id <> ?1 and e.phone = ?2")
    boolean existsByIdNotAndPhone(@NotNull Long id, @NotNull PhoneNumber phone);
    @Query("select (count(e) > 0) from EmployeeEntity e where e.phone = :phone")
    boolean existsByPhone(@Param("phone") @NonNull PhoneNumber phone);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Employee WHERE employee_id = :id", nativeQuery = true)
    void deleteEmployee(@Param("id") Long id);

    @EntityGraph(attributePaths = {"accountEntity"})
    @Query("select e from EmployeeEntity e")
    List<EmployeeEntity> findAllWithJoinFetch();

    @EntityGraph(attributePaths = {"accountEntity"})
    @Query("select e from EmployeeEntity e")
    Page<EmployeeEntity> findAllWithJoinFetch(Pageable pageable);

    @EntityGraph(attributePaths = {"accountEntity"})
    @Query("select e from EmployeeEntity e where e.id = :id")
    Optional<EmployeeEntity> findByIdWithJoinFetch(@NotNull @Param("id") Long id);

    @Query("select e from EmployeeEntity e where e.accountEntity.id = :id")
    Optional<EmployeeEntity> findByAccountEntity_Id(@Param("id") Long id);
    
    
}
