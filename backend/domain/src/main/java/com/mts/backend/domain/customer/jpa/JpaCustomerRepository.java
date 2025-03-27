package com.mts.backend.domain.customer.jpa;

import com.mts.backend.domain.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface JpaCustomerRepository extends JpaRepository<CustomerEntity, Long> {
  @Modifying
  @Transactional
  @Query(value = "INSERT INTO milk_tea_shop_prod.Customer (customer_id, account_id, membership_type_id, first_name, last_name, email, phone, current_points, gender) " +
           "VALUES (:#{#entity.id}, :#{#entity.accountEntity?.id}, :#{#entity.membershipTypeEntity.id}, :#{#entity.firstName}, :#{#entity.lastName}, :#{#entity.email}, :#{#entity.phone}, :#{#entity.currentPoints}, :#{#entity.gender?.name()})", nativeQuery = true)
  void insertCustomer(@Param("entity") CustomerEntity entity);

  @Modifying
  @Transactional
  @Query(value = "UPDATE milk_tea_shop_prod.Customer SET " +
          "account_id = :#{#entity.accountEntity?.id}, " +
          "membership_type_id = :#{#entity.membershipTypeEntity.id}, " +
          "first_name = :#{#entity.firstName}, " +
          "last_name = :#{#entity.lastName}, " +
          "email = :#{#entity.email}, " +
          "phone = :#{#entity.phone}, " +
            "current_points = :#{#entity.currentPoints}, " +
          "gender = :#{#entity.gender.name()} " +
            "WHERE customer_id = :#{#entity.id}",
          nativeQuery = true)
  void updateCustomer(@Param("entity") CustomerEntity entity);

  @Query("select (count(c) > 0) from CustomerEntity c where c.phone = :phone")
  boolean existsByPhone(@Param("phone") @NonNull String phone);

  @Query("select c from CustomerEntity c where c.phone = :phone")
  Optional<CustomerEntity> findByPhone(@Param("phone") @NonNull String phone);

  @Query("select c from CustomerEntity c where c.email = :email")
  Optional<CustomerEntity> findByEmail(@Param("email") @NonNull String email);

  @Query("select (count(c) > 0) from CustomerEntity c where c.email = :email")
  boolean existsByEmail(@Param("email") @NonNull String email);


}