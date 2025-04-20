package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateProductSizeCommand;
import com.mts.backend.domain.product.ProductSize;
import com.mts.backend.domain.product.UnitOfMeasure;
import com.mts.backend.domain.product.identifier.ProductSizeId;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.jpa.JpaProductSizeRepository;
import com.mts.backend.domain.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.domain.product.value_object.ProductSizeName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateProductSizeCommandHandler implements ICommandHandler<UpdateProductSizeCommand, CommandResult> {

    private final JpaProductSizeRepository sizeRepository;

    private final JpaUnitOfMeasureRepository unitRepository;

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

        try {
            var size = mustBeExistSize(command.getId());

            size.setName(command.getName());
            size.setUnit(unitRepository.getReferenceById(command.getUnitId().getValue()));
            size.setDescription(command.getDescription().orElse(null));
            size.setQuantity(command.getQuantity());

            return CommandResult.success(size.getId());
        } catch (DataIntegrityViolationException e) {

            if (e.getMessage().contains("uk_product_size_unit_name")) {
                throw new DuplicateException("Kích thước " + command.getName().getValue() + " với đơn vị "
                        + command.getUnitId().getValue() + " đã tồn tại");
            }

            if (e.getMessage().contains("fk_product_size_unit_of_measure")) {
                throw new NotFoundException("Đơn vị " + command.getUnitId().getValue() + " không tồn tại");
            }

            throw new DomainException("Lỗi khi cập nhật kích thước sản phẩm", e);
        }
    }

    private ProductSize mustBeExistSize(ProductSizeId sizeId) {
        return sizeRepository.findById(sizeId.getValue())
                .orElseThrow(() -> new NotFoundException("Size " + sizeId + " không tồn tại"));
    }


}
