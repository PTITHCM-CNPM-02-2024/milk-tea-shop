package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateProductSizeCommand;
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
        
                
        try{
            var productSize = ProductSize.builder()
                .id(ProductSizeId.create().getValue())
                .name(command.getName())
                .unit(unitOfMeasureRepository.getReferenceById(command.getUnitId().getValue()))
                .quantity(command.getQuantity())
                .description(command.getDescription().orElse(null))
                .build();
        
            var createdProductSize = sizeRepository.save(productSize);
            return CommandResult.success(createdProductSize.getId());
        }catch(DataIntegrityViolationException e){
            if(e.getMessage().contains("uk_product_size_unit_name")){
                throw new DuplicateException("Kích thước " + command.getName().getValue() + " đã tồn tại trong đơn vị " + command.getUnitId().getValue());
            }
            if(e.getMessage().contains("fk_product_size_unit_of_measure")){
                throw new NotFoundException("Đơn vị tính không tồn tại");
            }
            throw new DomainException("Lỗi khi tạo kích thước sản phẩm", e);
        }

    }
    
}
