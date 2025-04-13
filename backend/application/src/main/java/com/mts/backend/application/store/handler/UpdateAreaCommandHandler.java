package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.UpdateAreaCommand;
import com.mts.backend.domain.store.AreaEntity;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.domain.store.value_object.AreaName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateAreaCommandHandler implements ICommandHandler<UpdateAreaCommand, CommandResult> {
    private final JpaAreaRepository areaRepository;
    
    public UpdateAreaCommandHandler(JpaAreaRepository areaRepository){
        this.areaRepository = areaRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(UpdateAreaCommand command) {
        Objects.requireNonNull(command, "Update area command is required");
        
        var area = mustExistArea(command.getAreaId());
        
        if (area.changeAreaName(command.getName())){
            verifyUniqueName(command.getAreaId(), command.getName());
        }
        
        if(command.getMaxTable().isPresent() && area.getServiceTables().size() > command.getMaxTable().get().getValue()){
            throw new DomainException("Số bàn tối đa không được nhỏ hơn số bàn hiện tại");
        }
        
        if (!command.getActive()){
            area.getServiceTables().forEach(table -> {
                table.setActive(false); 
            });
        }
        
        area.setActive(command.getActive());
        
        area.changeDescription(command.getDescription());
        
        areaRepository.saveAndFlush(area);
        
        return CommandResult.success(area.getId());
        
    }
    
    private AreaEntity mustExistArea(AreaId areaId){
        return areaRepository.findById(areaId.getValue())
                .orElseThrow(() -> new NotFoundException("Khu vực" + areaId + " không tồn tại"));
    }
    
    private void verifyUniqueName(AreaId id, AreaName name) {
        Objects.requireNonNull(name, "Area name is required");
        if (areaRepository.existsByIdNotAndName(id.getValue(), name)) {
            throw new DuplicateException("Tên khu vực đã tồn tại");
        }
    }
}
