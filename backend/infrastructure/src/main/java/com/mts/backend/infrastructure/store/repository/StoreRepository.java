package com.mts.backend.infrastructure.store.repository;

import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.store.Store;
import com.mts.backend.domain.store.identifier.StoreId;
import com.mts.backend.domain.store.repository.IStoreRepository;
import com.mts.backend.domain.store.value_object.Address;
import com.mts.backend.domain.store.value_object.StoreName;
import com.mts.backend.infrastructure.persistence.entity.StoreEntity;
import com.mts.backend.infrastructure.store.jpa.JpaStoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class StoreRepository implements IStoreRepository {

    private final JpaStoreRepository jpaStoreRepository;

    public StoreRepository(JpaStoreRepository jpaStoreRepository) {
        this.jpaStoreRepository = jpaStoreRepository;
    }

    /**
     * @param storeId
     * @return
     */
    @Override
    public Optional<Store> findById(StoreId storeId) {
        return jpaStoreRepository.findById(storeId.getValue()).map(e -> new Store(
                StoreId.of(e.getId()),
                StoreName.of(e.getName()),
                Address.of(e.getAddress()),
                PhoneNumber.of(e.getPhone()),
                Email.of(e.getEmail()),
                e.getTaxCode(),
                e.getOpeningTime(),
                e.getClosingTime(),
                e.getUpdatedAt().orElse(LocalDateTime.now())
        ));
    }

    /**
     * @param store
     * @return
     */
    @Override
    @Transactional
    public Store save(Store store) {
        Objects.requireNonNull(store, "Store is required");
        
        try {
            if (jpaStoreRepository.existsById(store.getId().getValue())) {
                return update(store);
            }
            return create(store);
        } catch (Exception e) {
            throw e;
        }
    }
    
    @Transactional
    protected Store create(Store store){

        StoreEntity st = StoreEntity.builder()
                .name(store.getStoreName().getValue())
                .address(store.getAddress().getValue())
                .phone(store.getPhoneNumber().getValue())
                .taxCode(store.getTaxCode())
                .openingTime(store.getOpenTime())
                .closingTime(store.getCloseTime())
                .id(store.getId().getValue())
                .build();
        
        jpaStoreRepository.insertStore(st);
        
        return store;
    }
    
    @Transactional
    protected Store update(Store store){
        StoreEntity st = StoreEntity.builder()
                .name(store.getStoreName().getValue())
                .address(store.getAddress().getValue())
                .phone(store.getPhoneNumber().getValue())
                .taxCode(store.getTaxCode())
                .openingTime(store.getOpenTime())
                .closingTime(store.getCloseTime())
                .id(store.getId().getValue())
                .build();
        
        jpaStoreRepository.updateStore(st);
        
        return store;
    }
}
