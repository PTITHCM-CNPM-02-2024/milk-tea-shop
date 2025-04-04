package com.mts.backend.domain.staff.jpa;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.staff.ManagerEntity;
import com.mts.backend.domain.staff.identifier.ManagerId;
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
public interface JpaManagerRepository extends JpaRepository<ManagerEntity, Long> {
    @Query("select m from ManagerEntity m where m.email = :email")
    Optional<ManagerEntity> findByEmail(@Param("email") @NonNull Email email);
    

    @Query("select (count(m) > 0) from ManagerEntity m where m.email = :email")
    boolean existsByEmail(@Param("email") @NonNull Email email);

    @Query("select (count(m) > 0) from ManagerEntity m where m.phone = :phone")
    boolean existsByPhone(@Param("phone") @NonNull PhoneNumber phone);

    @Query("select (count(m) > 0) from ManagerEntity m where m.accountEntity.id = :id")
    boolean existsByAccountId(@Param("id") @NonNull Long id);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Manager WHERE manager_id = :id", nativeQuery = true)
    void deleteManager(@Param("id") Long id);
    
    
    @EntityGraph(attributePaths = {"accountEntity"})
    @Query("select m from ManagerEntity m")
    List<ManagerEntity> findAllWithJoinFetch();
    
    @EntityGraph(attributePaths = {"accountEntity"})
    @Query("select m from ManagerEntity m where m.id = :id")
    Optional<ManagerEntity> findByIdWithJoinFetch(@Param("id") @NonNull ManagerId id);

    @Query("select (count(m) > 0) from ManagerEntity m where m.id <> ?1 and m.phone = ?2")
    boolean existsByIdNotAndPhone(@NonNull Long id, @NonNull PhoneNumber phone);
    
    @Query("select (count(m) > 0) from ManagerEntity m where m.id <> ?1 and m.email = ?2")
    boolean existsByIdNotAndEmail(@NonNull Long id, @NonNull Email email);
}