package com.mts.backend.domain.customer.jpa;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.customer.CustomerEntity;
import com.mts.backend.domain.customer.identifier.CustomerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCustomerRepository extends JpaRepository<CustomerEntity, Long> {
  @Query("select c from CustomerEntity c where c.accountEntity.id = :id")
  Optional<CustomerEntity> findByAccountEntity_Id(@Param("id") Long id);

  @Query("select (count(c) > 0) from CustomerEntity c where c.accountEntity.id = :id")
  boolean existsByAccountEntity_Id(@Param("id") @NonNull Long id);

  @Query("select (count(c) > 0) from CustomerEntity c where c.phone = :phone")
  boolean existsByPhone(@Param("phone") @NonNull PhoneNumber phone);

  @Query("select c from CustomerEntity c where c.phone = :phone")
  Optional<CustomerEntity> findByPhone(@Param("phone") @NonNull PhoneNumber phone);

  @Query("select c from CustomerEntity c where c.email = :email")
  Optional<CustomerEntity> findByEmail(@Param("email") @NonNull Email email);

  @Query("select (count(c) > 0) from CustomerEntity c where c.email = :email")
  boolean existsByEmail(@Param("email") @NonNull Email email);


}