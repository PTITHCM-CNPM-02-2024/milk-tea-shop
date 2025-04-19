package com.mts.backend.domain.staff.jpa;

import com.mts.backend.domain.staff.ManagerEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaManagerRepository extends JpaRepository<ManagerEntity, Long> {
    @EntityGraph(attributePaths = {"account"})
    @Query("select m from ManagerEntity m where m.accountEntity.id = :id")
    Optional<ManagerEntity> findByAccountEntity_Id(@Param("id") @NonNull Long id);
    @Query("select m from ManagerEntity m where m.accountEntity.id = :id")
    Optional<ManagerEntity> findByAccountEntity_IdFetch(@Param("id") @NonNull Long id);
    @Query("select m from ManagerEntity m where m.email = :email")
    Optional<ManagerEntity> findByEmail(@Param("email") @NonNull String email);
    

    @Query("select (count(m) > 0) from ManagerEntity m where m.email = :email")
    boolean existsByEmail(@Param("email") @NonNull String email);

    @Query("select (count(m) > 0) from ManagerEntity m where m.phone = :phone")
    boolean existsByPhone(@Param("phone") @NonNull String phone);

    @Query("select (count(m) > 0) from ManagerEntity m where m.accountEntity.id = :id")
    boolean existsByAccountId(@Param("id") @NonNull Long id);
    
    
    
    @EntityGraph(attributePaths = {"account"})
    @Query("select m from ManagerEntity m")
    List<ManagerEntity> findAllWithJoinFetch();
    
    @EntityGraph(attributePaths = {"account"})
    @Query("select m from ManagerEntity m where m.id = :id")
    Optional<ManagerEntity> findByIdWithJoinFetch(@Param("id") @NonNull Long id);

    @Query("select (count(m) > 0) from ManagerEntity m where m.id <> ?1 and m.phone = ?2")
    boolean existsByIdNotAndPhone(@NonNull Long id, @NonNull String phone);
    
    @Query("select (count(m) > 0) from ManagerEntity m where m.id <> ?1 and m.email = ?2")
    boolean existsByIdNotAndEmail(@NonNull Long id, @NonNull String email);
}