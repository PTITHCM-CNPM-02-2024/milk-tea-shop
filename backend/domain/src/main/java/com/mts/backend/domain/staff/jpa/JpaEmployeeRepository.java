package com.mts.backend.domain.staff.jpa;

import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.staff.Employee;
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
public interface JpaEmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("select (count(e) > 0) from Employee e where e.account.id = :id")
    boolean existsByAccountId(@Param("id") @NonNull Long id);

    @Query("select (count(e) > 0) from Employee e where e.email = :email")
    boolean existsByEmail(@Param("email") @NonNull String email);

    @Query("select (count(e) > 0) from Employee e where e.id <> :id and e.email = :email")
    boolean existsByIdNotAndEmail(@Param("id") @NonNull Long id, @Param("email") @NonNull String email);
    @Query("select (count(e) > 0) from Employee e where e.id <> ?1 and e.phone = ?2")
    boolean existsByIdNotAndPhone(@NotNull Long id, @NotNull String phone);
    @Query("select (count(e) > 0) from Employee e where e.phone = :phone")
    boolean existsByPhone(@Param("phone") @NonNull String phone);
    

    @EntityGraph(attributePaths = {"account"})
    @Query("select e from Employee e")
    List<Employee> findAllWithJoinFetch();

    @EntityGraph(attributePaths = {"account"})
    @Query("select e from Employee e")
    Page<Employee> findAllWithJoinFetch(Pageable pageable);

    @EntityGraph(attributePaths = {"account"})
    @Query("select e from Employee e where e.id = :id")
    Optional<Employee> findByIdWithJoinFetch(@NotNull @Param("id") Long id);

    @Query("select e from Employee e where e.account.id = :id")
    Optional<Employee> findByAccountEntity_Id(@Param("id") Long id);
    
    @EntityGraph(attributePaths = {"account"})
    @Query("select e from Employee e where e.account.id = :id")
    Optional<Employee> findByAccountEntity_IdFetch(@Param("id") Long id);
}
