package com.mts.backend.infrastructure.staff.jpa;

import com.mts.backend.infrastructure.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaEmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    @Query("select (count(e) > 0) from EmployeeEntity e where e.accountEntity.id = :id")
    boolean existsByAccountId(@Param("id") @NonNull Long id);

    @Query("select (count(e) > 0) from EmployeeEntity e where e.email = :email")
    boolean existsByEmail(@Param("email") @NonNull String email);

    @Query("select (count(e) > 0) from EmployeeEntity e where e.phone = :phone")
    boolean existsByPhone(@Param("phone") @NonNull String phone);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.Employee (employee_id, account_id, first_name, last_name, email, phone, position, gender) " +
            "VALUES (:#{#entity.id}, :#{#entity.accountEntity.id}, :#{#entity.firstName}, :#{#entity.lastName}, " +
            ":#{#entity.email}, :#{#entity.phone}, :#{#entity.position}, :#{#entity.gender.name()})",
            nativeQuery = true)
    void insertEmployee(@Param("entity") EmployeeEntity entity);

    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.Employee SET " +
            "account_id = :#{#entity.accountEntity.id}, " +
            "first_name = :#{#entity.firstName}, " +
            "last_name = :#{#entity.lastName}, " +
            "email = :#{#entity.email}, " +
            "phone = :#{#entity.phone}, " +
            "position = :#{#entity.position}, " +
            "gender = :#{#entity.gender.name()} " +
            "WHERE employee_id = :#{#entity.id}",
            nativeQuery = true)
    void updateEmployee(@Param("entity") EmployeeEntity entity);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Employee WHERE employee_id = :id", nativeQuery = true)
    void deleteEmployee(@Param("id") Long id);
}
