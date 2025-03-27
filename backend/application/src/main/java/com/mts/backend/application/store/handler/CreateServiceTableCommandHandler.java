package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.CreateServiceTableCommand;
import com.mts.backend.domain.store.AreaEntity;
import com.mts.backend.domain.store.ServiceTableEntity;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.domain.store.value_object.TableNumber;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CreateServiceTableCommandHandler implements ICommandHandler<CreateServiceTableCommand, CommandResult> {
    private final JpaAreaRepository jpaAreaRepository;
    private final JpaServiceTableRepository jpaServiceTableRepository;
    public CreateServiceTableCommandHandler(JpaAreaRepository jpaAreaRepository, JpaServiceTableRepository jpaServiceTableRepository){
        this.jpaAreaRepository = jpaAreaRepository;
        this.jpaServiceTableRepository = jpaServiceTableRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(CreateServiceTableCommand command) {
        Objects.requireNonNull(command, "Create service table command is required");
        Optional<AreaEntity> area = mustExitTableIfSpecificAndNonMaxTable(command.getAreaId().orElse(null));
        

        TableNumber name = verifyUniqueName(command.getName());

        var isActive = area.map(AreaEntity::getActive).orElse(true);
        var serviceTable = ServiceTableEntity.builder()
                .id(ServiceTableId.create())
                .tableNumber(name)
                .areaEntity(area.orElse(null))
                .active(isActive)
                .build();

        var savedServiceTable = jpaServiceTableRepository.save(serviceTable);

        return CommandResult.success(savedServiceTable.getId().getValue());
    }

    @Transactional
    protected Optional<AreaEntity> mustExitTableIfSpecificAndNonMaxTable(AreaId areaId) {
        if (areaId == null) {
            return Optional.empty();
        }

        var area = jpaAreaRepository.findById(areaId).orElseThrow(() -> new NotFoundException("Khu vực" + areaId + " không tồn tại"));
        

        var count = jpaServiceTableRepository.countByAreaEntity_Id(areaId);

        if (area.getMaxTable().isPresent() && count >= area.getMaxTable().get().getValue()) {
            throw new NotFoundException("Khu vực đã đạt số bàn tối đa");
        }

        return Optional.of(area);
    }
    
    @Transactional
    protected TableNumber verifyUniqueName(TableNumber number) {
        Objects.requireNonNull(number, "TableNumber is required");

        if (jpaServiceTableRepository.existsByTableNumber(number.getValue())) {
            throw new DuplicateException("Tên bàn đã tồn tại");
        }

        return number;
    }
}
