package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateUnitCommand;
import com.mts.backend.domain.product.UnitOfMeasure;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.repository.IUnitRepository;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateUnitCommandHandler implements ICommandHandler<UpdateUnitCommand, CommandResult> {
    private final IUnitRepository unitRepository;
    
    public UpdateUnitCommandHandler(IUnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(UpdateUnitCommand command) {
        Objects.requireNonNull(command, "UpdateUnitCommand is required");
        
        var unit = mustBeExistUnit(UnitOfMeasureId.of(command.getId()));
        
        unit.changeDescription(command.getDescription());
        
        if (unit.changeName(command.getName())) {
            verifyUniqueName(unit.getName());
        }
        
        if (unit.changeSymbol(command.getSymbol())) {
            verifyUniqueSymbol(unit.getSymbol());
        }
        
        var updatedUnit = unitRepository.save(unit);
        
        return CommandResult.success(updatedUnit.getId().getValue());
    }
    
    private UnitOfMeasure mustBeExistUnit(UnitOfMeasureId unitId) {
        return unitRepository.findById(unitId)
                .orElseThrow(() -> new NotFoundException("Đơn vị " + unitId + " không tồn tại"));
    }
    
    private void verifyUniqueName(UnitName name) {
        Objects.requireNonNull(name, "Unit name is required");
        
        unitRepository.findByName(name).ifPresent(u -> {
            throw new NotFoundException("Đơn vị " + name.getValue() + " đã tồn tại");
        });
    }
    
    private void verifyUniqueSymbol(UnitSymbol symbol) {
        Objects.requireNonNull(symbol, "Unit symbol is required");
        
        unitRepository.findBySymbol(symbol).ifPresent(u -> {
            throw new NotFoundException("Đơn vị " + symbol + " đã tồn tại");
        });
    }
}
