package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.UpdateAreaCommand;
import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.domain.store.value_object.AreaName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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
        
        try{
            var area = mustExistArea(command.getAreaId());

            area.setName(command.getName());

            area.setMaxTable(command.getMaxTable().orElse(null));

            if (!command.getActive()){
                area.getServiceTables().forEach(table -> {
                    table.setActive(false);
                });
            }

            area.setActive(command.getActive());

            area.setDescription(command.getDescription());
            return CommandResult.success(area.getId());

        }catch(DataIntegrityViolationException e){
            if (e.getMessage().contains("uk_area_name")){
                throw new DuplicateException("Tên khu vực đã tồn tại");
            }
            throw new DomainException("Đã có lỗi xảy ra khi cập nhật khu vực", e);
        }
    }
    
    private Area mustExistArea(AreaId areaId){
        return areaRepository.findById(areaId.getValue())
                .orElseThrow(() -> new NotFoundException("Khu vực" + areaId + " không tồn tại"));
    }
    
}
