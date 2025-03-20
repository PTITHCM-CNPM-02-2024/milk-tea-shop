package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateProductSizeCommand;
import com.mts.backend.domain.product.ProductSize;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.repository.ISizeRepository;
import com.mts.backend.domain.product.repository.IUnitRepository;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import com.mts.backend.domain.product.value_object.QuantityOfProductSize;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateProductSizeCommandHandler implements ICommandHandler<UpdateProductSizeCommand, CommandResult> {


    private final ISizeRepository sizeRepository;

    private final IUnitRepository unitRepository;

    public UpdateProductSizeCommandHandler(ISizeRepository sizeRepository, IUnitRepository unitRepository) {
        this.sizeRepository = sizeRepository;
        this.unitRepository = unitRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    public CommandResult handle(UpdateProductSizeCommand command) {
        Objects.requireNonNull(command, "UpdateProductSizeCommand is required");

        var size = mustBeExistSize(ProductSizeId.of(command.getId()));

        if (size.changeName(ProductSizeName.of(command.getName())) || size.changeUnit(UnitOfMeasureId.of(command.getUnitId()))) {
            mustExistUnitOfMeasure(size.getUnitOfMeasure());
            verifyTupleUnique(size.getName(), size.getUnitOfMeasure());
        }
        
        size.changeDescription(command.getDescription());
        size.changeQuantity(QuantityOfProductSize.of(command.getQuantity()));

        var updatedSize = sizeRepository.save(size);

        return CommandResult.success(updatedSize.getId().getValue());
    }


    private ProductSize mustBeExistSize(ProductSizeId sizeId) {
        return sizeRepository.findById(sizeId)
                .orElseThrow(() -> new NotFoundException("Size " + sizeId + " không tồn tại"));
    }
    
    private void mustExistUnitOfMeasure(UnitOfMeasureId unitId) {
        Objects.requireNonNull(unitId, "UnitOfMeasureId is required");
        unitRepository.findById(unitId)
                .orElseThrow(() -> new NotFoundException("Unit " + unitId + " không tồn tại"));
    }

    private void verifyTupleUnique(ProductSizeName name, UnitOfMeasureId unit) {
        Objects.requireNonNull(name, "ProductSizeName is required");
        Objects.requireNonNull(unit, "UnitOfMeasureId is required");
        
        sizeRepository.findByNameAndUnit(name, unit)
                .ifPresent(size -> {
                    throw new NotFoundException("Size, Unit " + name.getValue() + ", " + unit.getValue() + " đã tồn tại");
                });
    }


}
