package com.mts.backend.infrastructure.product.repository;

import com.mts.backend.domain.product.UnitOfMeasure;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.repository.IUnitRepository;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import com.mts.backend.infrastructure.persistence.entity.UnitOfMeasureEntity;
import com.mts.backend.infrastructure.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class UnitRepository implements IUnitRepository {

    private final JpaUnitOfMeasureRepository jpaUnitOfMeasureRepository;

    public UnitRepository(JpaUnitOfMeasureRepository jpaUnitOfMeasureRepository) {
        this.jpaUnitOfMeasureRepository = jpaUnitOfMeasureRepository;
    }

    /**
     * @param unitOfMeasureId
     * @return
     */
    @Override
    public boolean existsById(UnitOfMeasureId unitOfMeasureId) {
        return false;
    }

    /**
     * @param unitOfMeasure
     * @return
     */
    @Transactional
    protected UnitOfMeasure create(UnitOfMeasure unitOfMeasure) {
        Objects.requireNonNull(unitOfMeasure, "Unit of measure is required");
        
        UnitOfMeasureEntity uom = UnitOfMeasureEntity.builder()
                .name(unitOfMeasure.getName().getValue())
                .symbol(unitOfMeasure.getSymbol().getValue())
                .description(unitOfMeasure.getDescription().orElse("")
                ).id(unitOfMeasure.getId().getValue()).build();
        
        jpaUnitOfMeasureRepository.insertUnitOfMeasure(uom);
        
        return unitOfMeasure;
    }

    /**
     * @param unitOfMeasureId
     * @return
     */
    @Override
    public Optional<UnitOfMeasure> findById(UnitOfMeasureId unitOfMeasureId) {
        Objects.requireNonNull(unitOfMeasureId, "Unit of measure id is required");

        try {

            return jpaUnitOfMeasureRepository.findById(unitOfMeasureId.getValue())
                    .map(uom -> new UnitOfMeasure(UnitOfMeasureId.of(uom.getId()), UnitName.of(uom.getName()), UnitSymbol.of(uom.getSymbol()), uom.getDescription(), uom.getCreatedAt().orElse(LocalDateTime.now()), uom.getUpdatedAt().orElse(LocalDateTime.now())));
        } catch (Exception e) {
            throw new DomainException("Không thể tìm đơn vị tính", e);
        }
    }
    
    /**
     * @param name
     * @return
     */
    @Override
    public Optional<UnitOfMeasure> findByName(UnitName name) {
        Objects.requireNonNull(name, "Unit name is required");

        return jpaUnitOfMeasureRepository.findByName(name.getValue())
                .map(uom -> new UnitOfMeasure(UnitOfMeasureId.of(uom.getId()), UnitName.of(uom.getName()), UnitSymbol.of(uom.getSymbol()), uom.getDescription(), uom.getCreatedAt().orElse(LocalDateTime.now()), uom.getUpdatedAt().orElse(LocalDateTime.now())));
    }

    /**
     * @param symbol
     * @return
     */
    @Override
    public Optional<UnitOfMeasure> findBySymbol(UnitSymbol symbol) {
        Objects.requireNonNull(symbol, "Unit of measure is required");
        return jpaUnitOfMeasureRepository.findBySymbol(symbol.getValue())
                .map(uom -> new UnitOfMeasure(UnitOfMeasureId.of(uom.getId()), UnitName.of(uom.getName()), UnitSymbol.of(uom.getSymbol()), uom.getDescription(), uom.getCreatedAt().orElse(LocalDateTime.now()), uom.getUpdatedAt().orElse(LocalDateTime.now())));
    }

    private void verifyUniqueName(UnitName name) {
        jpaUnitOfMeasureRepository.findByName(name.getValue())
                .ifPresent(uom -> {
                    UnitName check = UnitName.of(uom.getName());

                    if (check.equals(name)) {
                        throw new DuplicateException("Tên đơn vị " + name.getValue() + " đã tồn tại");
                    }
                });
    }

    private void verifyUniqueSymbol(UnitOfMeasure unitOfMeasure) {
        jpaUnitOfMeasureRepository.findBySymbol(unitOfMeasure.getSymbol().getValue())
                .ifPresent(uom -> {
                    if (uom.getSymbol().equals(unitOfMeasure.getSymbol().getValue())) {
                        throw new DuplicateException("Ký hiệu đơn vị " + unitOfMeasure.getSymbol().getValue() + " đã tồn tại");
                    }
                });
    }
    
    @Transactional
    public UnitOfMeasure save(UnitOfMeasure unitOfMeasure){
        Objects.requireNonNull(unitOfMeasure, "Unit of measure is required");
        
        try {
            if (jpaUnitOfMeasureRepository.existsById(unitOfMeasure.getId().getValue())) {
                return update(unitOfMeasure);
            }else {
                return create(unitOfMeasure);
            }
        }catch (RuntimeException e){
            throw new DomainException("Không thể lưu đơn vị tính", e);
        }
    }

    @Transactional
    protected UnitOfMeasure update(UnitOfMeasure unitOfMeasure) {
        Objects.requireNonNull(unitOfMeasure, "Unit of measure is required");

        try {
            UnitOfMeasureEntity uom = UnitOfMeasureEntity.builder()
                    .name(unitOfMeasure.getName().getValue())
                    .symbol(unitOfMeasure.getSymbol().getValue())
                    .description(unitOfMeasure.getDescription().orElse(""))
                    .id(unitOfMeasure.getId().getValue()).build();
            
            jpaUnitOfMeasureRepository.updateUnitOfMeasure(uom);
            
            return unitOfMeasure;
        } catch (Exception e) {
            throw new DomainException("Không thể cập nhật đơn vị tính", e);
        }
    }
}
