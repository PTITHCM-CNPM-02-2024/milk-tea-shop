package com.mts.backend.domain.store.jpa;

import com.mts.backend.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
public interface JpaStoreRepository extends JpaRepository<Store, Integer> {
    @Query("select count(s) from Store s where s.id is not null")
    long countByIdNotNull();

}
