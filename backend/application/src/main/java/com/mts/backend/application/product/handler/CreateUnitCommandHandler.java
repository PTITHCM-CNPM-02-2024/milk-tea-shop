package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.CreateUnitCommand;
import com.mts.backend.domain.product.UnitOfMeasure;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
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

        try {
            var unit = UnitOfMeasure.builder()
                    .id(UnitOfMeasureId.create().getValue())
                    .name(command.getName())
                    .symbol(command.getSymbol())
                    .description(command.getDescription().orElse(null))
                    .build();

            var createdUnit = unitRepository.save(unit);

            return CommandResult.success(createdUnit.getId());
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_unit_of_measure_name")) {
                throw new DuplicateException("Đơn vị " + command.getName().getValue() + " đã tồn tại");
            }
            if (e.getMessage().contains("uk_unit_of_measure_symbol")) {
                throw new DuplicateException("Kí hiệu " + command.getSymbol().getValue() + " đã tồn tại");
            }
            throw new DomainException("Lỗi khi tạo đơn vị tính", e);
        }

    }

}
