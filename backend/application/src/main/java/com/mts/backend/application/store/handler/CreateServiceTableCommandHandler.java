package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.CreateServiceTableCommand;
import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.ServiceTable;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CreateServiceTableCommandHandler implements ICommandHandler<CreateServiceTableCommand, CommandResult> {
    private final JpaAreaRepository jpaAreaRepository;
    private final JpaServiceTableRepository jpaServiceTableRepository;

    public CreateServiceTableCommandHandler(JpaAreaRepository jpaAreaRepository, JpaServiceTableRepository jpaServiceTableRepository) {
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
        Optional<Area> area = command.getAreaId()
                .map(AreaId::getValue)
                .flatMap(jpaAreaRepository::findById);

        var isActive = area.map(Area::getActive).orElse(true);

        var serviceTable = ServiceTable.builder()
                .id(ServiceTableId.create().getValue())
                .tableNumber(command.getName())
                .active(isActive)
                .build();
        
        serviceTable.setArea(area.orElse(null));
        try {

            var savedServiceTable = jpaServiceTableRepository.save(serviceTable);

            return CommandResult.success(savedServiceTable.getId());
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_service_table_name")) {
                throw new DuplicateException("Tên bàn đã tồn tại");
            }
            if(e.getMessage().contains("fk_service_table_area")){
                throw new DomainException("Khu vực không tồn tại");
            }
            throw new DomainException("Lỗi không xác định khi tạo bàn", e);
        }
    }


}
