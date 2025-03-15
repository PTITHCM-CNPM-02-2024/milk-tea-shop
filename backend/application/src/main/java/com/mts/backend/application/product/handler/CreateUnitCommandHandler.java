package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateUnitCommand;
import com.mts.backend.domain.product.UnitOfMeasure;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.repository.IUnitRepository;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateUnitCommandHandler implements ICommandHandler<CreateUnitCommand, CommandResult> {

    private final IUnitRepository unitRepository;

    public CreateUnitCommandHandler(IUnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    public CommandResult handle(CreateUnitCommand command) {

        Objects.requireNonNull(command.getName(), "Unit name is required");

        UnitOfMeasure unit = new UnitOfMeasure(
                UnitOfMeasureId.create(),
                UnitName.of(command.getName()),
                UnitSymbol.of(command.getSymbol()),
                command.getDescription().orElse(""),
                command.getCreatedAt().orElse(LocalDateTime.now()),
                command.getUpdatedAt().orElse(LocalDateTime.now())
        );

        // Check for duplicate name
        verifyUniqueName(unit.getName());

        // Check for duplicate symbol
        verifyUniqueSymbol(unit.getSymbol());

        UnitOfMeasure createdUnit = unitRepository.save(unit);

        return CommandResult.success(createdUnit.getId().getValue());

    }
    
    private void verifyUniqueName(UnitName name) {
        unitRepository.findByName(name).ifPresent(unit -> {
            throw new DuplicateException("Đơn vị " + name.getValue() + " đã tồn tại");
        });
    }
    
    private void verifyUniqueSymbol(UnitSymbol symbol) {
            unitRepository.findBySymbol(symbol).ifPresent(unit -> {
            throw new DuplicateException("Ký hiệu " + symbol + " đã tồn tại");
        });
    }
}
