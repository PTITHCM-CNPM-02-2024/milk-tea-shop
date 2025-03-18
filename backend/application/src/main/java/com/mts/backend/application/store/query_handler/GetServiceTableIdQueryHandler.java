package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.ServiceTableByIdQuery;
import com.mts.backend.application.store.response.ServiceTableDetailResponse;
import com.mts.backend.domain.store.ServiceTable;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.repository.IServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class GetServiceTableIdQueryHandler implements IQueryHandler<ServiceTableByIdQuery, CommandResult> {
    
    private final IServiceTableRepository serviceTableRepository;
    private final IAreaRepository areaRepository;
    
    public GetServiceTableIdQueryHandler(IServiceTableRepository serviceTableRepository, IAreaRepository areaRepository) {
        this.serviceTableRepository = serviceTableRepository;
        this.areaRepository = areaRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(ServiceTableByIdQuery query) {
        Objects.requireNonNull(query, "Get service table by id query is required");
        
        var re = mustExistServiceTable(ServiceTableId.of(query.getId()));
        
        ServiceTableDetailResponse response = ServiceTableDetailResponse.builder()
                .id(re.getId().getValue())
                .name(re.getTableNumber().getValue())
                .isActive(re.isActive()).build();
        
        if (re.getAreaId().isPresent()){
            verifyAreaIfExist(re.getAreaId().get());
        }
        
        response.setAreaId(re.getAreaId().map(AreaId::getValue).orElse(null));
        
        return CommandResult.success(response);
    }
    
    private ServiceTable mustExistServiceTable(ServiceTableId id){
        return serviceTableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bàn không tồn tại"));
    }
    
    private void verifyAreaIfExist(AreaId areaId){
        if (areaId != null && !areaRepository.existsById(areaId)){
            throw new NotFoundException("Khu vực không tồn tại");
        }
    }
}
