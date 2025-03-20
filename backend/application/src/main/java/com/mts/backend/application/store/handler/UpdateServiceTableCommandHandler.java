package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.UpdateServiceTableCommand;
import com.mts.backend.domain.store.ServiceTable;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.repository.IServiceTableRepository;
import com.mts.backend.domain.store.value_object.TableNumber;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class UpdateServiceTableCommandHandler implements ICommandHandler<UpdateServiceTableCommand, CommandResult> {
    private final IServiceTableRepository serviceTableRepository;
    private final IAreaRepository areaRepository;

    public UpdateServiceTableCommandHandler(IServiceTableRepository serviceTableRepository,
                                            IAreaRepository areaRepository) {
        this.serviceTableRepository = serviceTableRepository;
        this.areaRepository = areaRepository;
    }

    /**
     * @param command
     * @return
     */
    @Override
    public CommandResult handle(UpdateServiceTableCommand command) {
        Objects.requireNonNull(command, "Update service table command is required");
        
        var serviceTable = mustExistTable(ServiceTableId.of(command.getId()));
        
        if (serviceTable.changeTableNumber(TableNumber.of(command.getName()))){
            verifyUniqueName(serviceTable.getTableNumber());
        }


        AreaId areaId = command.getAreaId() != null ? mustExistArea(AreaId.of(command.getAreaId())) : null;
        serviceTable.changeAreaId(areaId);
         serviceTable.changeIsActive(command.getIsActive());
         
         if (serviceTable.isActive() && areaId != null){
             allowActiveWhenAreaExistAndActive(areaId);
         }

        var savedServiceTable = serviceTableRepository.save(serviceTable);

        return CommandResult.success(savedServiceTable.getId().getValue());
    }
    
    private ServiceTable mustExistTable(ServiceTableId id){
        Objects.requireNonNull(id, "Service table id is required");
        
        return serviceTableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bàn không tồn tại"));
    }

    private void verifyUniqueName(TableNumber number) {
        Objects.requireNonNull(number, "Table number is required");

        if (serviceTableRepository.existsByName(number)) {
            throw new DuplicateException("Số bàn đã tồn tại");
        }
    }

    private AreaId mustExistArea(AreaId areaId) {
        Objects.requireNonNull(areaId, "Area id is required");

        if (!areaRepository.existsById(areaId)) {
            throw new NotFoundException("Khu vực không tồn tại");
        }

        return areaId;
    }
    
    private void allowActiveWhenAreaExistAndActive(AreaId areaId){
        if (areaId == null){
            return;
        }
        
        var area = areaRepository.findById(areaId)
                .orElseThrow(() -> new NotFoundException("Khu vực không tồn tại"));
        
        if (!area.isActive()){
            throw new NotFoundException("Khu vực không hoạt động, không thể kích hoạt bàn");
        }
    }
}