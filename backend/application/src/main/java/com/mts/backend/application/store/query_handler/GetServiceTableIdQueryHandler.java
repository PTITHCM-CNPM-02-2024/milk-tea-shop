package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.ServiceTableByIdQuery;
import com.mts.backend.application.store.response.ServiceTableDetailResponse;
import com.mts.backend.domain.store.ServiceTable;
import com.mts.backend.domain.store.ServiceTableEntity;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.repository.IServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class GetServiceTableIdQueryHandler implements IQueryHandler<ServiceTableByIdQuery, CommandResult> {
    
    private final JpaServiceTableRepository serviceTableRepository;
    
    public GetServiceTableIdQueryHandler(JpaServiceTableRepository serviceTableRepository, IAreaRepository areaRepository) {
        this.serviceTableRepository = serviceTableRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(ServiceTableByIdQuery query) {
        Objects.requireNonNull(query, "Get service table by id query is required");
        
        var re = mustExistServiceTable(query.getId());
        
        ServiceTableDetailResponse response = ServiceTableDetailResponse.builder()
                .id(re.getId().getValue())
                .name(re.getTableNumber().getValue())
                .isActive(re.getActive())
                        .areaId(re.getAreaEntity().map(a -> a.getId().getValue()).orElse(null))
                        .build();
        
        return CommandResult.success(response);
    }
    
    private ServiceTableEntity mustExistServiceTable(ServiceTableId id){
        return serviceTableRepository.findByIdWithArea(id)
                .orElseThrow(() -> new NotFoundException("Bàn không tồn tại"));
    }
}
