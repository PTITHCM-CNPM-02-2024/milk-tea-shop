package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.UpdateServiceTableCommand;
import com.mts.backend.domain.store.ServiceTable;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.domain.store.value_object.TableNumber;
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
public class UpdateServiceTableCommandHandler implements ICommandHandler<UpdateServiceTableCommand, CommandResult> {

    private final JpaServiceTableRepository jpaServiceTableRepository;
    private final JpaAreaRepository jpaAreaRepository;

    public UpdateServiceTableCommandHandler(JpaServiceTableRepository jpaServiceTableRepository,
                                            JpaAreaRepository jpaAreaRepository) {
        this.jpaServiceTableRepository = jpaServiceTableRepository;
        this.jpaAreaRepository = jpaAreaRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(UpdateServiceTableCommand command) {
        Objects.requireNonNull(command, "Update service table command is required");
        Objects.requireNonNull(command.getId(), "Service table id is required");

        try{
            // Find the service table
            ServiceTable serviceTable = findServiceTable(command.getId());

            serviceTable.setTableNumber(command.getName());

            // Update area if provided
            updateAreaIfNeeded(serviceTable, command.getAreaId().orElse(null));

            // Update active status and validate
            serviceTable.setActive(command.isActive());

            // Save changes
            return CommandResult.success(serviceTable.getId());
        }catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_service_table_name")) {
                throw new DuplicateException("Tên bàn đã tồn tại");
            }
            if (e.getMessage().contains("uk_service_table_area")) {
                throw new DuplicateException("Bàn đã tồn tại trong khu vực này");
            }
            throw new DomainException("Đã có lỗi xảy ra khi cập nhật bàn", e);
        }
    }

    @Transactional
    protected ServiceTable findServiceTable(ServiceTableId id) {
        return jpaServiceTableRepository.findById(id.getValue())
                .orElseThrow(() -> new NotFoundException("Bàn không tồn tại"));
    }

    

    private void updateAreaIfNeeded(ServiceTable serviceTable, AreaId areaId) {
        if (areaId == null) {
            serviceTable.setArea(null);
            return;
        }
        serviceTable.setArea(jpaAreaRepository.getReferenceById(areaId.getValue()));
    }
}