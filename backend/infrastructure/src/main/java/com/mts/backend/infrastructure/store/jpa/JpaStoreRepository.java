package com.mts.backend.infrastructure.store.jpa;

import com.mts.backend.infrastructure.persistence.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface JpaStoreRepository extends JpaRepository<StoreEntity, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO milk_tea_shop_prod.Store (store_id, name, address, phone, email, tax_code, " +
            "opening_time, closing_time, opening_date) " +
            "VALUES (:#{#entity.id}, :#{#entity.name}, :#{#entity.address}, :#{#entity.phone}, :#{#entity.email}, " +
            ":#{#entity.taxCode}, :#{#entity.openingTime}, :#{#entity.closingTime}, :#{#entity.openingDate})", nativeQuery = true)
    void insertStore(@Param("entity") StoreEntity entity);

    @Modifying
    @Transactional
    @Query(value = "UPDATE milk_tea_shop_prod.Store SET " +
            "name = :#{#entity.name}, " +
            "address = :#{#entity.address}, " +
            "phone = :#{#entity.phone}, " +
            "email = :#{#entity.email}, " +
            "tax_code = :#{#entity.taxCode}, " +
            "opening_time = :#{#entity.openingTime}, " +
            "closing_time = :#{#entity.closingTime}, " +
            "opening_date = :#{#entity.openingDate} " +
            "WHERE store_id = :#{#entity.id}",
            nativeQuery = true)
    void updateStore(@Param("entity") StoreEntity entity);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM milk_tea_shop_prod.Store WHERE store_id = :id", nativeQuery = true)
    void deleteStore(@Param("id") Integer id);
}
