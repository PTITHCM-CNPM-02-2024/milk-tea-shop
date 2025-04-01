package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateUnitCommand;
import com.mts.backend.domain.product.UnitOfMeasureEntity;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateUnitCommandHandler implements ICommandHandler<UpdateUnitCommand, CommandResult> {
    private final JpaUnitOfMeasureRepository unitRepository;
    
    public UpdateUnitCommandHandler(JpaUnitOfMeasureRepository unitRepository) {
        this.unitRepository = unitRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(UpdateUnitCommand command) {
        Objects.requireNonNull(command, "UpdateUnitCommand is required");
        
        var unit = mustBeExistUnit(command.getId());
        
        unit.setDescription(command.getDescription().orElse(null));
        
        if (unit.changeUnitName(command.getName())) {
            verifyUniqueName(command.getId(), unit.getName());
        }
        
        if (unit.changeUnitSymbol(command.getSymbol())) {
            verifyUniqueSymbol(command.getId(), unit.getSymbol());
        }
        
        
        
        return CommandResult.success(unit.getId());
    }
    
    private UnitOfMeasureEntity mustBeExistUnit(UnitOfMeasureId unitId) {
        return unitRepository.findById(unitId.getValue())
                .orElseThrow(() -> new NotFoundException("Đơn vị " + unitId + " không tồn tại"));
    }
    
    private void verifyUniqueName(UnitOfMeasureId id, UnitName name) {
        Objects.requireNonNull(name, "Unit name is required");
        
        if (unitRepository.existsByIdNotAndName(id.getValue(), name)) {
            throw new NotFoundException("Đơn vị " + name.getValue() + " đã tồn tại");
        }
    }
    
    private void verifyUniqueSymbol(UnitOfMeasureId id, UnitSymbol symbol) {
        Objects.requireNonNull(symbol, "Unit symbol is required");
        
        if (unitRepository.existsByIdNotAndSymbol(id.getValue(), symbol)) {
            throw new NotFoundException("Đơn vị " + symbol.getValue() + " đã tồn tại");
        }
    }
}
