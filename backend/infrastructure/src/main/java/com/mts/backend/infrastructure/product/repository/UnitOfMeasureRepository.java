package com.mts.backend.infrastructure.product.repository;

import com.mts.backend.domain.product.UnitOfMeasure;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.repository.IUnitOfMeasureRepository;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import com.mts.backend.infrastructure.persistence.entity.UnitOfMeasureEntity;
import com.mts.backend.infrastructure.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class UnitOfMeasureRepository implements IUnitOfMeasureRepository {

    private final JpaUnitOfMeasureRepository jpaUnitOfMeasureRepository;

    public UnitOfMeasureRepository(JpaUnitOfMeasureRepository jpaUnitOfMeasureRepository) {
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
    @Override
    public UnitOfMeasure create(UnitOfMeasure unitOfMeasure) {
        Objects.requireNonNull(unitOfMeasure, "Unit of measure is required");

        UnitOfMeasureEntity uom = UnitOfMeasureEntity.builder().name(unitOfMeasure.getName().getValue())
                .symbol(unitOfMeasure.getSymbol().getValue())
                .description(unitOfMeasure.getDescription().orElse(""))
                .id(null)
                .build();

        jpaUnitOfMeasureRepository.save(uom);

        return new UnitOfMeasure(UnitOfMeasureId.of(uom.getId()), UnitName.of(uom.getName()), UnitSymbol.of(uom.getSymbol()), uom.getDescription(), uom.getCreatedAt().orElse(LocalDateTime.now()), uom.getUpdatedAt().orElse(LocalDateTime.now()));
   
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
     * @param unitOfMeasure
     * @return
     */
    @Override
    public void save(UnitOfMeasure unitOfMeasure) {
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
}
