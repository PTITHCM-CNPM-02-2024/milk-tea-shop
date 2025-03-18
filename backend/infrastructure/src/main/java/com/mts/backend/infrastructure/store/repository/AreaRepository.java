package com.mts.backend.infrastructure.store.repository;

import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.value_object.AreaName;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.infrastructure.persistence.entity.AreaEntity;
import com.mts.backend.infrastructure.store.jpa.JpaAreaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
public class AreaRepository implements IAreaRepository {
    
    private final JpaAreaRepository jpaAreaRepository;
    
    public AreaRepository(JpaAreaRepository jpaAreaRepository){
        this.jpaAreaRepository = jpaAreaRepository;
    }
    /**
     * @param areaId 
     * @return
     */
    @Override
    public Optional<Area> findById(AreaId areaId) {
        Objects.requireNonNull(areaId, "Area id is required");
        
        return jpaAreaRepository.findById(areaId.getValue()).map(e -> new Area(
                AreaId.of(e.getId()),
                AreaName.of(e.getName()),
                e.getDescription(),
                e.getMaxTables() != null ? MaxTable.of(e.getMaxTables()) : null,
                e.getIsActive(),
                e.getUpdatedAt().orElse(LocalDateTime.now())
        ));
    }

    /**
     * @param area 
     * @return
     */
    @Override
    @Transactional
    public Area save(Area area) {
        Objects.requireNonNull(area, "Area is required");
        
        try {
            if (jpaAreaRepository.existsById(area.getId().getValue())){
                return update(area);
            }
            return create(area);
        }catch (Exception e){
            throw e;
        }
    }

    @Transactional
    protected Area create(Area area){
        Objects.requireNonNull(area, "Area is required");
        
        var en = AreaEntity.builder()
                .id(area.getId().getValue())
                .name(area.getAreaName().getValue())
                .description(area.getDescription().orElse(null))
                .maxTables(area.getMaxTable().map(MaxTable::getValue).orElse(null))
                .isActive(area.isActive())
                .build();
        
         jpaAreaRepository.insertArea(en);
        
        return area;
                
    }
    
    @Transactional
    protected Area update(Area area){
        Objects.requireNonNull(area, "Area is required");
        
        var en = AreaEntity.builder()
                .id(area.getId().getValue())
                .name(area.getAreaName().getValue())
                .description(area.getDescription().orElse(null))
                .maxTables(area.getMaxTable().map(MaxTable::getValue).orElse(null))
                .isActive(area.isActive())
                .build();
        
        jpaAreaRepository.updateArea(en);
        
        return area;
    }
    /**
     * @param areaId 
     * @return
     */
    @Override
    public boolean existsById(AreaId areaId) {
        Objects.requireNonNull(areaId, "Area id is required");
        
        return jpaAreaRepository.existsById(areaId.getValue());
    }

    /**
     * @param name 
     * @return
     */
    @Override
    public boolean existsByName(AreaName name) {
        Objects.requireNonNull(name, "Area name is required");
        
        return jpaAreaRepository.existsByName(name.getValue());
    }

    /**
     * @param name 
     * @return
     */
    @Override
    public Optional<Area> findByName(AreaName name) {
        Objects.requireNonNull(name, "Area name is required");
        
        return jpaAreaRepository.findByName(name.getValue()).map(e -> new Area(
                AreaId.of(e.getId()),
                AreaName.of(e.getName()),
                e.getDescription(),
                e.getMaxTables() != null ? MaxTable.of(e.getMaxTables()) : null,
                e.getIsActive(),
                e.getUpdatedAt().orElse(LocalDateTime.now())
        ));
    }

    /**
     * @return 
     */
    @Override
    public List<Area> findAll() {
        return jpaAreaRepository.findAll().stream().map(e -> new Area(
                AreaId.of(e.getId()),
                AreaName.of(e.getName()),
                e.getDescription(),
                e.getMaxTables() != null ? MaxTable.of(e.getMaxTables()) : null,
                e.getIsActive(),
                e.getUpdatedAt().orElse(LocalDateTime.now())
        )).toList();
    }

    /**
     * @return 
     */
    @Override
    public List<Area> findAllActive() {
        return findAll().stream().filter(Area::isActive).toList();
    }
}
