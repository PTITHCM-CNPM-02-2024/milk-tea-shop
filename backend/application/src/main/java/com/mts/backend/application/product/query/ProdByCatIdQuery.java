package com.mts.backend.application.product.query;

import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProdByCatIdQuery implements IQuery<CommandResult> {
    private CategoryId id;
    private Boolean availableOrder;
    private Integer page;
    private Integer size;
    
    public Optional<Boolean> getAvailableOrder() {
        return Optional.ofNullable(availableOrder);
    }
    
    public Optional<CategoryId> getId() {
        return Optional.ofNullable(id);
    }
}
