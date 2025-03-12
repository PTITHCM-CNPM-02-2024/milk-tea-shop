package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateProductSizeCommand;
import com.mts.backend.domain.product.ProductSize;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.repository.ISizeRepository;
import com.mts.backend.domain.product.repository.IUnitOfMeasureRepository;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import com.mts.backend.domain.product.value_object.QuantityOfProductSize;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateProductSizeCommandHandler implements ICommandHandler<CreateProductSizeCommand, CommandResult> {

    private final ISizeRepository sizeRepository;
    
    private final IUnitOfMeasureRepository unitOfMeasureRepository;

    public CreateProductSizeCommandHandler(ISizeRepository sizeRepository, IUnitOfMeasureRepository unitOfMeasureRepository) {
        this.sizeRepository = sizeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    public CommandResult handle(CreateProductSizeCommand command) {
        Objects.requireNonNull(command.getName(), "Product size name is required");

        mustExistUnitOfMeasure(UnitOfMeasureId.of(command.getUnitId()));

        ProductSize productSize = new ProductSize(
                ProductSizeId.create(),
                ProductSizeName.of(command.getName()),
                UnitOfMeasureId.of(command.getUnitId()),
                QuantityOfProductSize.of(command.getQuantity()),
                command.getDescription().orElse(""),
                command.getCreatedAt().orElse(LocalDateTime.now()),
                command.getUpdatedAt().orElse(LocalDateTime.now())
        );

        verifyUniqueName(productSize.getName());

        ProductSize createdProductSize = sizeRepository.create(productSize);

        return CommandResult.success(createdProductSize.getId().getValue());

    }
    
    private void verifyUniqueName(ProductSizeName name) {
        Objects.requireNonNull(name, "ProductSizeName is required");
        sizeRepository.findByName(name).ifPresent(productSize -> {
            throw new DuplicateException("Kích thước " + name.getValue() + " đã tồn tại");
        });
    }
    
    private void mustExistUnitOfMeasure(UnitOfMeasureId unitOfMeasureId) {
        Objects.requireNonNull(unitOfMeasureId, "UnitOfMeasureId is required");
        unitOfMeasureRepository.findById(unitOfMeasureId).orElseThrow(() -> new NotFoundException("Đơn vị tính không tồn tại"));
    }
}
