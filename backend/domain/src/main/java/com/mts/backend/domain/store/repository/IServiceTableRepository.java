package com.mts.backend.domain.store.repository;

import com.mts.backend.domain.store.ServiceTable;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.value_object.TableNumber;

import java.util.List;
import java.util.Optional;

public interface IServiceTableRepository {
    
    Optional<ServiceTable> findById(ServiceTableId serviceTableId);
    
    ServiceTable save(ServiceTable serviceTable);
    
    boolean existsById(ServiceTableId serviceTableId);
    
    boolean existsByName(TableNumber name);
    
    Optional<ServiceTable> findByTableNumber(TableNumber tableNumber);
    
    List<ServiceTable> findAll();
    
    List<ServiceTable> findAllActive();
    
    List<ServiceTable> findAllByAreaId(AreaId areaId);
}
