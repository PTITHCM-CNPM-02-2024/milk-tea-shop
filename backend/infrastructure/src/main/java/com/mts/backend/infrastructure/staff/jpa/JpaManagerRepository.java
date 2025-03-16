package com.mts.backend.infrastructure.staff.jpa;

import com.mts.backend.infrastructure.persistence.entity.ManagerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface JpaManagerRepository extends JpaRepository<ManagerEntity, Long> {
    @Query("select m from ManagerEntity m where m.email = :email")
    Optional<ManagerEntity> findByEmail(@Param("email") @NonNull String email);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.Manager (manager_id, account_id, last_name, first_name, email, gender, phone) " +
            "VALUES (:#{#entity.id}, :#{#entity.accountEntity.id}, :#{#entity.lastName}, :#{#entity.firstName}, :#{#entity.email}, :#{#entity.gender.name()} , :#{#entity.phone}" + ")", nativeQuery = true)
    void insertManager(@Param("entity") ManagerEntity entity);

    @Query("select (count(m) > 0) from ManagerEntity m where m.email = :email")
    boolean existsByEmail(@Param("email") @NonNull String email);

    @Query("select (count(m) > 0) from ManagerEntity m where m.phone = :phone")
    boolean existsByPhone(@Param("phone") @NonNull String phone);

    @Query("select (count(m) > 0) from ManagerEntity m where m.accountEntity.id = :id")
    boolean existsByAccountId(@Param("id") @NonNull Long id);


    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.Manager SET " +
            "last_name = :#{#entity.lastName}, " +
            "first_name = :#{#entity.firstName}, " +
            "email = :#{#entity.email}, " +
            "gender = :#{#entity.gender.name()}, " +
            "phone = :#{#entity.phone} ," +
            "account_id = :#{#entity.accountEntity.id} " +
            "WHERE manager_id = :#{#entity.id}", nativeQuery = true)
    void updateManager(@Param("entity") ManagerEntity entity);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Manager WHERE manager_id = :id", nativeQuery = true)
    void deleteManager(@Param("id") Long id);
    
    
}