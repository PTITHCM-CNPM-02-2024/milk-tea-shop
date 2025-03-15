package com.mts.backend.infrastructure.account.jpa;

import com.mts.backend.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, Integer> {
    @Query("select (count(r) > 0) from RoleEntity r where r.name = :name")
    boolean existsByName(@Param("name") String name);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.Role (role_id, name, description) " +
            "VALUES (:#{#entity.id}, :#{#entity.name}, :#{#entity.description})", nativeQuery = true)
    void insertRole(@Param("entity") RoleEntity entity);

    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.Role SET " +
            "name = :#{#entity.name}, " +
            "description = :#{#entity.description} " +
            "WHERE role_id = :#{#entity.id}", nativeQuery = true)
    void updateRole(@Param("entity") RoleEntity entity);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Role WHERE role_id = :id", nativeQuery = true)
    void deleteRole(@Param("id") Short id);

}