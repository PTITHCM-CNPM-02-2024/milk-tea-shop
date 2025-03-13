package com.mts.backend.domain.product.repository;

import com.mts.backend.domain.product.UnitOfMeasure;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;

import java.util.Optional;

public interface IUnitRepository {
    boolean existsById(UnitOfMeasureId unitOfMeasureId);
    
    Optional<UnitOfMeasure> findById(UnitOfMeasureId unitOfMeasureId);
    
    
    /**TODO: replace create method with save method
     * @param unitOfMeasure
     * @return
     */
    UnitOfMeasure save(UnitOfMeasure unitOfMeasure);
    
    Optional<UnitOfMeasure> findByName(UnitName name);
    
    Optional<UnitOfMeasure> findBySymbol(UnitSymbol unitOfMeasure);
}
