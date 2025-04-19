package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.AreaByIdQuery;
import com.mts.backend.application.store.response.AreaDetailResponse;
import com.mts.backend.application.store.response.ServiceTableSummaryResponse;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetAreaByIdQueryHandler implements IQueryHandler<AreaByIdQuery, CommandResult> {
    
    private final JpaAreaRepository areaRepository;
    
    public GetAreaByIdQueryHandler(JpaAreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(AreaByIdQuery query) {
        Objects.requireNonNull(query,"GetAreaByIdQuery is required");
        
        var area = areaRepository.findByIdFetch(query.getId().getValue())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy khu vực"));

        AreaDetailResponse response = AreaDetailResponse.builder().id(area.getId())
                .name(area.getName().getValue())
                .description(area.getDescription().orElse(null))
                .maxTable(area.getMaxTable().map(MaxTable::getValue).orElse(null))
                .isActive(area.getActive())
                .areas(area.getServiceTables().stream().map(serviceTable -> {
                    ServiceTableSummaryResponse serviceTableResponse = ServiceTableSummaryResponse.builder()
                            .id(serviceTable.getId())
                            .name(serviceTable.getTableNumber().getValue())
                            .isActive(serviceTable.getActive())
                            .build();
                    return serviceTableResponse;
                }).toList())
                .build();
        
        return CommandResult.success(response);
    }
}
