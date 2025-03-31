package com.mts.backend.domain.account.jpa;

import com.mts.backend.domain.account.RoleEntity;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, Integer> {
    @Query("select (count(r) > 0) from RoleEntity r where r.name = :name")
    boolean existsByName(@Param("name") RoleName name);

    @Query("select (count(r) > 0) from RoleEntity r where r.id <> :id and r.name = :name")
    boolean existsByIdNotAndName(@Param("id") @NonNull Integer id, @Param("name") @NonNull RoleName name);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Role WHERE role_id = :id", nativeQuery = true)
    void deleteRole(@Param("id") Short id);

}