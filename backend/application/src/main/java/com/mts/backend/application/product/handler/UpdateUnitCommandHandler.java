package com.mts.backend.application.product.handler;

import com.mts.backend.application.product.command.UpdateUnitCommand;
import com.mts.backend.domain.product.UnitOfMeasure;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
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
    @Transactional
    public CommandResult handle(UpdateUnitCommand command) {
        Objects.requireNonNull(command, "UpdateUnitCommand is required");

        try {
            var unit = mustBeExistUnit(command.getId());

            unit.setDescription(command.getDescription().orElse(null));
            unit.setName(command.getName());
            unit.setSymbol(command.getSymbol());

            unitRepository.saveAndFlush(unit);
            return CommandResult.success(unit.getId());
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_unit_of_measure_name")) {
                throw new DuplicateException("Đơn vị " + command.getName().getValue() + " đã tồn tại");
            }
            if (e.getMessage().contains("uk_unit_of_measure_symbol")) {
                throw new DuplicateException("Kí hiệu " + command.getSymbol().getValue() + " đã tồn tại");
            }
            throw new DomainException("Lỗi khi cập nhật đơn vị tính", e);
        }
    }

    private UnitOfMeasure mustBeExistUnit(UnitOfMeasureId unitId) {
        return unitRepository.findById(unitId.getValue())
                .orElseThrow(() -> new NotFoundException("Đơn vị " + unitId + " không tồn tại"));
    }

}
