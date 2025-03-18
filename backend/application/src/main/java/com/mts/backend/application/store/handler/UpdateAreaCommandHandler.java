package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.UpdateAreaCommand;
import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.value_object.AreaName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateAreaCommandHandler implements ICommandHandler<UpdateAreaCommand, CommandResult> {
    private final IAreaRepository areaRepository;
    
    public UpdateAreaCommandHandler(IAreaRepository areaRepository){
        this.areaRepository = areaRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(UpdateAreaCommand command) {
        Objects.requireNonNull(command, "Update area command is required");
        
        var area = mustExistArea(AreaId.of(command.getAreaId()));
        
        if (area.changeAreaName(AreaName.of(command.getName()))){
            verifyUniqueName(area.getAreaName());
        }
        
        area.changeDescription(command.getDescription());
        
        var savedArea = areaRepository.save(area);
        
        return CommandResult.success(savedArea.getId().getValue());
        
    }
    
    private Area mustExistArea(AreaId areaId){
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new NotFoundException("Khu vực không tồn tại"));
    }
    
    private void verifyUniqueName(AreaName name){
        Objects.requireNonNull(name, "Area name is required");
        if (areaRepository.existsByName(name)){
            throw new DuplicateException("Tên khu vực đã tồn tại");
        }
    }
}
