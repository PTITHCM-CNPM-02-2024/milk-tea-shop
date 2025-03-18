package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.CreateServiceTableCommand;
import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.ServiceTable;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.repository.IServiceTableRepository;
import com.mts.backend.domain.store.value_object.TableNumber;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class CreateServiceTableCommandHandler implements ICommandHandler<CreateServiceTableCommand, CommandResult> {
    private final IServiceTableRepository serviceTableRepository;
    private final IAreaRepository areaRepository;

    public CreateServiceTableCommandHandler(IServiceTableRepository serviceTableRepository,
                                            IAreaRepository areaRepository) {
        this.serviceTableRepository = serviceTableRepository;
        this.areaRepository = areaRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    public CommandResult handle(CreateServiceTableCommand command) {
        Objects.requireNonNull(command, "Create service table command is required");
        Optional<Area> area = mustExitTableIfSpecificAndNonMaxTable(AreaId.of(command.getAreaId()));
        
        AreaId areaId = area.map(AbstractAggregateRoot::getId).orElse(null);
        
        TableNumber name = verifyUniqueName(TableNumber.of(command.getName()));
        
        var isActive = area.map(Area::isActive).orElse(true);
        var serviceTable = new ServiceTable(
                ServiceTableId.create(),
                name,
                isActive,
                areaId,
                LocalDateTime.now());

        var savedServiceTable = serviceTableRepository.save(serviceTable);

        return CommandResult.success(savedServiceTable.getId().getValue());
    }

    private Optional<Area> mustExitTableIfSpecificAndNonMaxTable(AreaId areaId) {
        Objects.requireNonNull(areaId, "AreaId is required");

        var area = areaRepository.findById(areaId);
        if (area.isEmpty()) {
            throw new NotFoundException("Khu vực không tồn tại");
        }

        var count = serviceTableRepository.findAllByAreaId(areaId).size();

        if (area.get().getMaxTable().isPresent() && count >= area.get().getMaxTable().get().getValue()) {
            throw new NotFoundException("Khu vực đã đạt số bàn tối đa");
        }

        return area;
    }

    private TableNumber verifyUniqueName(TableNumber number) {
        Objects.requireNonNull(number, "TableNumber is required");

        if (serviceTableRepository.existsByName(number)) {
            throw new DuplicateException("Tên bàn đã tồn tại");
        }

        return number;
    }
}
