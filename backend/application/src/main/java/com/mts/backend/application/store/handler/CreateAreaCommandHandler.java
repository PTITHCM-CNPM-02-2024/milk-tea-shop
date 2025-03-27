package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.CreateAreaCommand;
import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.AreaEntity;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.value_object.AreaName;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateAreaCommandHandler implements ICommandHandler<CreateAreaCommand, CommandResult> {
    private final JpaAreaRepository areaRepository;
    
    
    public CreateAreaCommandHandler(JpaAreaRepository areaRepository){
        this.areaRepository = areaRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(CreateAreaCommand command) {
        Objects.requireNonNull(command, "Create area command is required");
        
        var area = AreaEntity.builder()
                .id(AreaId.create())
                .name(command.getName())
                .description(command.getDescription())
                .maxTable(command.getMaxTable().orElse(null))
                .active(command.isActive())
                .build();
        
        verifyUniqueName(area.getName());
        
        var savedArea = areaRepository.save(area);
        
        return CommandResult.success(savedArea.getId().getValue());
    }
    
    protected void verifyUniqueName(AreaName name){
        Objects.requireNonNull(name, "Area name is required");
        if (areaRepository.existsByName(name.getValue())){
            throw new DuplicateException("Tên khu vực đã tồn tại");
        }
    }
}
