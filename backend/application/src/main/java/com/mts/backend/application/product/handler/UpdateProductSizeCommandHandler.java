package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateProductSizeCommand;
import com.mts.backend.domain.product.ProductSizeEntity;
import com.mts.backend.domain.product.UnitOfMeasureEntity;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.jpa.JpaProductSizeRepository;
import com.mts.backend.domain.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UpdateProductSizeCommandHandler implements ICommandHandler<UpdateProductSizeCommand, CommandResult> {


    private final JpaProductSizeRepository sizeRepository;

    private final JpaUnitOfMeasureRepository unitRepository;
    
    private final static List<ProductSizeId> EXCLUDE_IDS = List.of(
            ProductSizeId.of(1),
            ProductSizeId.of(2),
            ProductSizeId.of(3),
            ProductSizeId.of(4)
            );

    public UpdateProductSizeCommandHandler(
            JpaProductSizeRepository sizeRepository,
            JpaUnitOfMeasureRepository unitRepository) {
        this.sizeRepository = sizeRepository;
        this.unitRepository = unitRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(UpdateProductSizeCommand command) {
        Objects.requireNonNull(command, "UpdateProductSizeCommand is required");
        
        if (EXCLUDE_IDS.contains(command.getId())) {
            throw new NotFoundException("Kích thước không thể thay đổi");
        }
        

        var size = mustBeExistSize(command.getId());
        var unit = mustExistUnitOfMeasure(command.getUnitId());
        

        verifyTupleUnique(command.getId(), command.getUnitId(), command.getName());
        size.changeName(command.getName());
        size.setUnit(unit);
        size.changeDescription(command.getDescription().orElse(null));
        size.changeQuantity(command.getQuantity());


        return CommandResult.success(size.getId());
    }


    private ProductSizeEntity mustBeExistSize(ProductSizeId sizeId) {
        return sizeRepository.findById(sizeId.getValue())
                .orElseThrow(() -> new NotFoundException("Size " + sizeId + " không tồn tại"));
    }

    private UnitOfMeasureEntity mustExistUnitOfMeasure(UnitOfMeasureId unitId) {
        Objects.requireNonNull(unitId, "UnitOfMeasureId is required");

        if (!unitRepository.existsById(unitId.getValue())) {
            throw new NotFoundException("Đơn vị " + unitId.getValue() + " không tồn tại");
        }

        return unitRepository.getReferenceById(unitId.getValue());
    }

    private void verifyTupleUnique(ProductSizeId id,  UnitOfMeasureId unit, ProductSizeName name) {
        Objects.requireNonNull(name, "ProductSizeName is required");
        Objects.requireNonNull(unit, "UnitOfMeasureId is required");

        if (sizeRepository.existsByIdNotAndUnit_IdAndName(id.getValue(), unit.getValue(),name)) {
            throw new NotFoundException("Kích thước " + name.getValue() + " với đơn vị " + unit.getValue() + " đã tồn tại");
        }
    }


}
