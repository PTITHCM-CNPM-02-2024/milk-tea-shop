package com.mts.backend.domain.account.jpa;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.domain.account.value_object.Username;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface JpaAccountRepository extends JpaRepository<AccountEntity, Long> {
  @Query("select a from AccountEntity a where a.username = :username")
  Optional<AccountEntity> findByUsername(@Param("username") @NonNull Username username);

  @Query("select (count(a) > 0) from AccountEntity a where a.id <> :id and a.username = :username")
  boolean existsByIdNotAndUsername(@Param("id") @NonNull Long id, @Param("username") @NonNull Username username);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM milk_tea_shop_prod.Account WHERE account_id = :id",
          nativeQuery = true)
  void deleteAccount(@Param("id") Long id);
  
    @Query("select (count(a) > 0) from AccountEntity a where a.username = :username")
    boolean existsByUsername(@Param("username") Username username);
    
    @Query(value = """
                   CALL milk_tea_shop_prod.sp_grant_permissions_by_role(:p_account_id)
                   """, nativeQuery = true)
    List<Object[]> grantPermissionsByRole(@Param("p_account_id") Long p_account_id);
    
    
    @Query(value = """
                   CALL milk_tea_shop_prod.sp_revoke_permissions(:p_account_id)
                   """, nativeQuery = true)
    List<Object[]> revokePermissionsByRole(@Param("p_account_id") Long p_account_id);
    
    @Query(value = """
                   CALL milk_tea_shop_prod.sp_lock_unlock_account(:p_account_id, :p_locked)
                   """, nativeQuery = true)
    List<Object[]> lockUnlockAccount(@Param("p_account_id") Long p_account_id, @Param("p_locked") Boolean p_locked);
}