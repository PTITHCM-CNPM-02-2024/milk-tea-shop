package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.ChangeProductInformCommand;
import com.mts.backend.domain.product.identifier.ProductId;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.domain.product.value_object.ProductName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ChangeProductInformCommandHandler implements ICommandHandler<ChangeProductInformCommand, CommandResult> {
    private final IProductRepository productRepository;
    
    public ChangeProductInformCommandHandler(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(ChangeProductInformCommand command) {
        Objects.requireNonNull(command.getProductId(), "Product id is required");
        
        var product = productRepository.findById(ProductId.of(command.getProductId()))
                .orElseThrow(() -> new NotFoundException("Sản phẩm " + command.getProductId() + " không tồn tại"));
        
        product.changeName(command.getName());
        product.changeAvailable(command.isAvailable());
        product.changeSignature(command.isSignature());
        product.changeDescription(command.getDescription());
        
        productRepository.updateInform(product);
        
        return CommandResult.success("Thay đổi thông tin sản phẩm thành công");
    }
}

