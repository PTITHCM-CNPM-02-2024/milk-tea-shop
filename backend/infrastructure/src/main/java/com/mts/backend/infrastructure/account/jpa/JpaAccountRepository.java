package com.mts.backend.infrastructure.account.jpa;

import com.mts.backend.infrastructure.persistence.entity.AccountEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO milk_tea_shop_prod.Account (account_id, role_id, username, password_hash, is_active, is_locked, last_login, token_version) " +
          "VALUES (:#{#entity.id}, :#{#entity.roleEntity.id}, :#{#entity.username}, :#{#entity.passwordHash}, :#{#entity.isActive}, :#{#entity.isLocked}, :#{#entity.lastLogin}, :#{#entity.tokenVersion})",
          nativeQuery = true)
  void insertAccount(@Param("entity") AccountEntity entity);

  @Modifying
  @Transactional
  @Query(value = "UPDATE milk_tea_shop_prod.Account SET " +
          "role_id = :#{#entity.roleEntity.id}, " +
          "username = :#{#entity.username}, " +
          "password_hash = :#{#entity.passwordHash}, " +
          "is_locked = :#{#entity.isLocked}, " +
          "token_version = :#{#entity.tokenVersion}, " +
          "is_active = :#{#entity.isActive}, " +
          "last_login = :#{#entity.lastLogin} " +
          "WHERE account_id = :#{#entity.id}",
          nativeQuery = true)
  void updateAccount(@Param("entity") AccountEntity entity);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM milk_tea_shop_prod.Account WHERE account_id = :id",
          nativeQuery = true)
  void deleteAccount(@Param("id") Long id);
  
    @Query("select (count(a) > 0) from AccountEntity a where a.username = :username")
    boolean existsByUsername(@Param("username") String username);
}