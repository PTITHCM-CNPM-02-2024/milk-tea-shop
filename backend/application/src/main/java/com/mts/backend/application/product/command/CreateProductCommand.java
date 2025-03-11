package com.mts.backend.application.product.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CreateProductCommand implements ICommand<CommandResult> {
    private    String name;
    private  String description;
    private  Integer categoryId;
    private  boolean available;
    private  boolean signature;
    private  String imagePath;
    private  LocalDateTime createdAt;
    private  LocalDateTime updatedAt;
    private  Map<Integer, BigDecimal> prices = new HashMap<>();
    
    public Optional<Integer> getCategoryId() {
        return Optional.ofNullable(categoryId);
    }
    
    public Optional<LocalDateTime> getCreatedAt() {
        return Optional.ofNullable(createdAt);
    }
    
    public Optional<LocalDateTime> getUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }
    
    public Optional<Map<Integer, BigDecimal>> getPrices() {
        return Optional.of(prices);
    }
    
    public Optional<String> getImagePath() {
        return Optional.ofNullable(imagePath);
    }
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    
    
}
