package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.ServiceTableByIdQuery;
import com.mts.backend.application.store.response.AreaDetailResponse;
import com.mts.backend.application.store.response.ServiceTableDetailResponse;
import com.mts.backend.domain.store.AreaEntity;
import com.mts.backend.domain.store.ServiceTableEntity;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class GetServiceTableIdQueryHandler implements IQueryHandler<ServiceTableByIdQuery, CommandResult> {
    
    private final JpaServiceTableRepository serviceTableRepository;
    
    public GetServiceTableIdQueryHandler(JpaServiceTableRepository serviceTableRepository) {
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
                .id(re.getId())
                .name(re.getTableNumber().getValue())
                .isActive(re.getActive())
                        .area(re.getAreaEntity().map(area -> AreaDetailResponse.builder()
                                .id(area.getId())
                                .name(area.getName().getValue())
                                .maxTable(area.getMaxTable().map(maxTable -> maxTable.getValue()).orElse(null))
                                .isActive(area.getActive())
                                .description(area.getDescription().orElse(null))
                                .build()).orElse(null)).build();
        
        return CommandResult.success(response);
    }
    
    private ServiceTableEntity mustExistServiceTable(ServiceTableId id){
        return serviceTableRepository.findByIdWithArea(id.getValue())
                .orElseThrow(() -> new NotFoundException("Bàn không tồn tại"));
    }
}
