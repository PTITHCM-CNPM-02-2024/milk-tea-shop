package com.mts.backend.domain.customer.jpa;

import com.mts.backend.domain.customer.Customer;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCustomerRepository extends JpaRepository<Customer, Long> {
  @Query("select c from Customer c where c.account.id = :id")
  Optional<Customer> findByAccountEntity_Id(@Param("id") Long id);

  @EntityGraph(attributePaths = {"membershipType", "account"})
  @Query("select c from Customer c where c.account.id = :id")
  Optional<Customer> findByAccountEntity_IdFetch(@Param("id") Long id);

  @Query("select (count(c) > 0) from Customer c where c.account.id = :id")
  boolean existsByAccountEntity_Id(@Param("id") @NonNull Long id);

  @Query("select (count(c) > 0) from Customer c where c.phone = :phone")
  boolean existsByPhone(@Param("phone") @NonNull String phone);

  @Query("select (count(c) > 0) from Customer c where c.id <> :id and c.phone = :phone")
  boolean existsByIdNotAndPhone(@Param("id") @NonNull Long id, @Param("phone") @NonNull String phone);

  @Query("select c from Customer c where c.phone = :phone")
  Optional<Customer> findByPhone(@Param("phone") @NonNull String phone);

  @Query("select c from Customer c where c.email = :email")
  Optional<Customer> findByEmail(@Param("email") @NonNull String email);

  @Query("select (count(c) > 0) from Customer c where c.email = :email")
  boolean existsByEmail(@Param("email") @NonNull String email);

    @EntityGraph(attributePaths = {"membershipType.memberDiscountValue"})
    @Query("select c from Customer c where c.id = :id")
  Optional<Customer> findByIdFetchMembershipType(@NotNull @Param("id") Long id);
    
    @EntityGraph(attributePaths = {"membershipType.memberDiscountValue", "account"})
    @Query("select c from Customer c")
    Page<Customer> findAllFetch(Pageable pageable);
    
    
    @EntityGraph(value = "graph.customer.fetchMembershipTypeAndAccount", type = EntityGraph.EntityGraphType.FETCH)
    @Query("select c from Customer c where c.id = :id")
    Optional<Customer> findByIdFetchMembershipTypeAndAccount(@NotNull @Param("id") Long id);

  @Query("select (count(c) > 0) from Customer c where c.id <> :id and c.email = :email")
  boolean existsByIdNotAndEmail(@Param("id") @NonNull Long id, @Param("email") @NonNull String email);
}