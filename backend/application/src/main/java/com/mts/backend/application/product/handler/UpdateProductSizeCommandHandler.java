package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateProductSizeCommand;
import com.mts.backend.domain.product.ProductSize;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.repository.ISizeRepository;
import com.mts.backend.domain.product.repository.IUnitRepository;
import com.mts.backend.domain.product.value_object.ProductSizeName;
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

        if (size.changeName(ProductSizeName.of(command.getName()))) {
            verifyUniqueName(size.getName());
        }

        if (size.changeUnit(UnitOfMeasureId.of(command.getUnitId()))) {
            verifyUniqueUnit(size.getUnitOfMeasure());
        }
        size.changeDescription(command.getDescription());
        size.changeQuantity(command.getQuantity());

        var updatedSize = sizeRepository.save(size);

        return CommandResult.success(updatedSize.getId().getValue());
    }


    private ProductSize mustBeExistSize(ProductSizeId sizeId) {
        return sizeRepository.findById(sizeId)
                .orElseThrow(() -> new NotFoundException("Size " + sizeId + " không tồn tại"));
    }

    private void verifyUniqueName(ProductSizeName name) {
        Objects.requireNonNull(name, "Size name is required");

        sizeRepository.findByName(name).ifPresent(s -> {
            throw new NotFoundException("Size " + name.getValue() + " đã tồn tại");
        });
    }

    private void verifyUniqueUnit(UnitOfMeasureId unit) {
        Objects.requireNonNull(unit, "Unit is required");

        if (!unitRepository.existsById(unit)) {
            throw new NotFoundException("Đơn vị " + unit + " không tồn tại");
        }
    }


}
