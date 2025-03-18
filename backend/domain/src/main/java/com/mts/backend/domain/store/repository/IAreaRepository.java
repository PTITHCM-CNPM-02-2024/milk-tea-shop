package com.mts.backend.domain.store.repository;

import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.value_object.AreaName;

import java.util.List;
import java.util.Optional;

public interface IAreaRepository {
    
    Optional<Area> findById(AreaId areaId);
    
    Area save(Area area);
    
    boolean existsById(AreaId areaId);
    
    boolean existsByName(AreaName name);
    
    Optional<Area> findByName(AreaName name);
    
    List<Area> findAll();
    
    List<Area> findAllActive();
}
