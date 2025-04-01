package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateProductSizeCommand;
import com.mts.backend.domain.product.ProductSizeEntity;
import com.mts.backend.domain.product.UnitOfMeasureEntity;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.jpa.JpaProductSizeRepository;
import com.mts.backend.domain.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateProductSizeCommandHandler implements ICommandHandler<CreateProductSizeCommand, CommandResult> {

    private final JpaProductSizeRepository sizeRepository;
    
    private final JpaUnitOfMeasureRepository unitOfMeasureRepository;

    public CreateProductSizeCommandHandler(JpaProductSizeRepository sizeRepository,
                                           JpaUnitOfMeasureRepository unitOfMeasureRepository) {
        this.sizeRepository = sizeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(CreateProductSizeCommand command) {
        Objects.requireNonNull(command.getName(), "Product size name is required");
        

        var un  = verifyUnitOfMeasure(command.getUnitId());
        
        verifyTupleUnique(command.getName(), command.getUnitId());
        
        var productSize = ProductSizeEntity.builder()
                .id(ProductSizeId.create().getValue())
                .name(command.getName())
                .unit(un)
                .quantity(command.getQuantity())
                .description(command.getDescription().orElse(null))
                .build();
        
        var createdProductSize = sizeRepository.save(productSize);
        return CommandResult.success(createdProductSize.getId());

    }
    
    private void verifyTupleUnique(ProductSizeName name, UnitOfMeasureId unit) {
        Objects.requireNonNull(name, "ProductSizeName is required");
        Objects.requireNonNull(unit, "UnitOfMeasureId is required");
        
        if (sizeRepository.existsByNameAndUnit_Id(name, unit.getValue())) {
            throw new DuplicateException("Kích thước " + name.getValue() + " đã tồn tại trong đơn vị " + unit.getValue());
        }
    }
    
    private UnitOfMeasureEntity verifyUnitOfMeasure(UnitOfMeasureId unitOfMeasureId) {
        Objects.requireNonNull(unitOfMeasureId, "UnitOfMeasureId is required");
        
        if (!unitOfMeasureRepository.existsById(unitOfMeasureId.getValue())){
            throw new NotFoundException("Đơn vị " + unitOfMeasureId.getValue() + " không tồn tại");
        }
        
        return unitOfMeasureRepository.getReferenceById(unitOfMeasureId.getValue());
    }
}
