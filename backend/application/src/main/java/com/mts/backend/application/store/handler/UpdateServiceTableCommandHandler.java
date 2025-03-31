package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.UpdateServiceTableCommand;
import com.mts.backend.domain.store.ServiceTableEntity;
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

        // Find the service table
        ServiceTableEntity serviceTable = findServiceTable(command.getId());

        // Update table number if provided
        if (serviceTable.changeTableNumber(command.getName())){
            verifyUniqueTableNumber(command.getId(), command.getName());
        }
        // Update area if provided
        updateAreaIfNeeded(serviceTable, command.getAreaId().orElse(null));

        // Update active status and validate
        updateActiveStatus(serviceTable, command.isActive());

        // Save changes
        ServiceTableEntity savedServiceTable = jpaServiceTableRepository.save(serviceTable);

        return CommandResult.success(savedServiceTable.getId());
    }

    @Transactional
    protected ServiceTableEntity findServiceTable(ServiceTableId id) {
        return jpaServiceTableRepository.findById(id.getValue())
                .orElseThrow(() -> new NotFoundException("Bàn không tồn tại"));
    }

    @Transactional
    protected void verifyUniqueTableNumber(ServiceTableId id, TableNumber number) {
        Objects.requireNonNull(number, "Table number is required");

         if(jpaServiceTableRepository.existsByIdNotAndTableNumber(id.getValue(), number)) {
            throw new DuplicateException("Bàn " + number + " đã tồn tại");
         }
    }

    private void updateAreaIfNeeded(ServiceTableEntity serviceTable, AreaId areaId) {
        if (areaId == null) {
            serviceTable.setAreaEntity(null);
            return;
        }
        if (!jpaAreaRepository.existsById(areaId.getValue())) {
            throw new NotFoundException("Khu vực không tồn tại");
        }
        
        serviceTable.setAreaEntity(jpaAreaRepository.getReferenceById(areaId.getValue()));
    }

    private void updateActiveStatus(ServiceTableEntity serviceTable, boolean isActive) {
        // Only need to validate when activating
        if (isActive) {
            validateAreaForActiveTable(serviceTable);
        }
        serviceTable.changeIsActive(isActive);
    }

    private void validateAreaForActiveTable(ServiceTableEntity serviceTable) {
        if (serviceTable.getAreaEntity().isEmpty()) return;
        if (!serviceTable.getAreaEntity().get().getActive()) {
            throw new DomainException("Khu vực" + serviceTable.getAreaEntity().get().getName() + " không hoạt động");
        }
    }
}