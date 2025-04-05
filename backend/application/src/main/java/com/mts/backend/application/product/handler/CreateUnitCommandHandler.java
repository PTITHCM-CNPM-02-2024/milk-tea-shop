package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateUnitCommand;
import com.mts.backend.domain.product.UnitOfMeasureEntity;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateUnitCommandHandler implements ICommandHandler<CreateUnitCommand, CommandResult> {

    private final JpaUnitOfMeasureRepository unitRepository;

    public CreateUnitCommandHandler(JpaUnitOfMeasureRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(CreateUnitCommand command) {

        Objects.requireNonNull(command.getName(), "Unit name is required");

        // Check for duplicate name
        verifyUniqueName(command.getName());

        // Check for duplicate symbol
        verifyUniqueSymbol(command.getSymbol());

        var unit = UnitOfMeasureEntity.builder()
                .id(UnitOfMeasureId.create().getValue())
                .name(command.getName())
                .symbol(command.getSymbol())
                .description(command.getDescription().orElse(null))
                .build();
        
        var createdUnit = unitRepository.save(unit);

        return CommandResult.success(createdUnit.getId());

    }
    
    private void verifyUniqueName(UnitName name) {
        Objects.requireNonNull(name, "Unit name is required");
        if(unitRepository.existsByName(name)) {
            throw new DuplicateException("Đơn vị " + name.getValue() + " đã tồn tại");
        }
    }
    
    private void verifyUniqueSymbol(UnitSymbol symbol) {
        Objects.requireNonNull(symbol, "Unit symbol is required");
       if(unitRepository.existsBySymbol(symbol)) {
            throw new DuplicateException("Kí hiệu " + symbol.getValue() + " đã tồn tại");
        }
    }
}
