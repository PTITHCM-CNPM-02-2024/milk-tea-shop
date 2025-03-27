package com.mts.backend.domain.store.repository;

import com.mts.backend.domain.store.ServiceTable;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.value_object.TableNumber;
import com.mts.backend.domain.store.AreaEntity;
import com.mts.backend.domain.store.ServiceTableEntity;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ServiceTableRepository implements IServiceTableRepository {

    private final JpaServiceTableRepository jpaServiceTableRepository;

    public ServiceTableRepository(JpaServiceTableRepository jpaServiceTableRepository) {
        this.jpaServiceTableRepository = jpaServiceTableRepository;
    }

    /**
     * @param serviceTableId
     * @return
     */
    @Override
    public Optional<ServiceTable> findById(ServiceTableId serviceTableId) {
        Objects.requireNonNull(serviceTableId, "Service table id is required");
        
        return jpaServiceTableRepository.findById(serviceTableId.getValue()).map(e -> new ServiceTable(
                ServiceTableId.of(e.getId()),
                TableNumber.of(e.getTableNumber()),
                e.getActive(),
                e.getAreaEntity() != null ? AreaId.of(e.getAreaEntity().getId()) : null,
                e.getUpdatedAt().orElse(LocalDateTime.now())
        ));
    }

    /**
     * @param serviceTable
     * @return
     */
    @Override
    @Transactional
    public ServiceTable save(ServiceTable serviceTable) {
        Objects.requireNonNull(serviceTable, "Service table is required");
        
        try {
            if (jpaServiceTableRepository.existsById(serviceTable.getId().getValue())) {
                return update(serviceTable);
            }
            return create(serviceTable);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    protected ServiceTable create(ServiceTable serviceTable) {
        Objects.requireNonNull(serviceTable, "Service table is required");

        ServiceTableEntity en = ServiceTableEntity.builder()
                .id(serviceTable.getId().getValue())
                .tableNumber(serviceTable.getTableNumber().getValue())
                .isActive(serviceTable.isActive())
                .build();

        AreaEntity areaEntity = AreaEntity.builder().id(serviceTable.getAreaId().map(AreaId::getValue).orElse(null)).build();

        en.setAreaEntity(areaEntity);

        jpaServiceTableRepository.insertServiceTable(en);

        return serviceTable;


    }
    
    @Transactional
    protected ServiceTable update(ServiceTable serviceTable){
        Objects.requireNonNull(serviceTable, "Service table is required");
        
        ServiceTableEntity en = ServiceTableEntity.builder()
                .id(serviceTable.getId().getValue())
                .tableNumber(serviceTable.getTableNumber().getValue())
                .isActive(serviceTable.isActive())
                .build();
        
        AreaEntity areaEntity = AreaEntity.builder().id(serviceTable.getAreaId().map(AreaId::getValue).orElse(null)).build();
        
        en.setAreaEntity(areaEntity);
        
        jpaServiceTableRepository.updateServiceTable(en);
        
        return serviceTable;
    }

    /**
     * @param serviceTableId
     * @return
     */
    @Override
    public boolean existsById(ServiceTableId serviceTableId) {
        Objects.requireNonNull(serviceTableId, "Service table id is required");
        
        return jpaServiceTableRepository.existsById(serviceTableId.getValue());
    }

    /**
     * @param name
     * @return
     */
    @Override
    public boolean existsByName(TableNumber name) {
        Objects.requireNonNull(name, "Table number is required");
        
        return jpaServiceTableRepository.existsByTableNumber(name.getValue());
    }

    /**
     * @param tableNumber
     * @return
     */
    @Override
    public Optional<ServiceTable> findByTableNumber(TableNumber tableNumber) {
        Objects.requireNonNull(tableNumber, "Table number is required");
        
        return jpaServiceTableRepository.findByTableNumber(tableNumber.getValue()).map(e -> new ServiceTable(
                ServiceTableId.of(e.getId()),
                TableNumber.of(e.getTableNumber()),
                e.getActive(),
                e.getAreaEntity() != null ? AreaId.of(e.getAreaEntity().getId()) : null,
                e.getUpdatedAt().orElse(LocalDateTime.now())
        ));
    }

    /**
     * @return
     */
    @Override
    public List<ServiceTable> findAll() {
        return jpaServiceTableRepository.findAll().stream().map(e -> new ServiceTable(
                ServiceTableId.of(e.getId()),
                TableNumber.of(e.getTableNumber()),
                e.getActive(),
                e.getAreaEntity() != null ? AreaId.of(e.getAreaEntity().getId()) : null,
                e.getUpdatedAt().orElse(LocalDateTime.now())
        )).toList();
    }

    /**
     * @return
     */
    @Override
    public List<ServiceTable> findAllActive() {
        return findAll().stream().filter(ServiceTable::isActive).toList();
    }
    
    /**
     * @param areaId
     * @return
     */
    
    @Override
    public List<ServiceTable> findAllByAreaId(AreaId areaId) {
        Objects.requireNonNull(areaId, "Area id is required");
        
        return jpaServiceTableRepository.findByAreaEntity_Id(areaId.getValue()).stream().map(e -> new ServiceTable(
                ServiceTableId.of(e.getId()),
                TableNumber.of(e.getTableNumber()),
                e.getActive(),
                e.getAreaEntity() != null ? AreaId.of(e.getAreaEntity().getId()) : null,
                e.getUpdatedAt().orElse(LocalDateTime.now())
        )).toList();
    }
}
