package com.mts.backend.domain.store.repository;

import com.mts.backend.domain.store.Store;
import com.mts.backend.domain.store.identifier.StoreId;

import java.util.Optional;

public interface IStoreRepository {
    Optional<Store> findById(StoreId storeId);
    
    Store save(Store store);
}
