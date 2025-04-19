package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.ServiceTableActiveQuery;
import com.mts.backend.application.store.response.ServiceTableSummaryResponse;
import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GetAllServiceTableActiveQueryHandler implements IQueryHandler<ServiceTableActiveQuery, CommandResult> {
    private final JpaServiceTableRepository serviceTableRepository;
    
    public GetAllServiceTableActiveQueryHandler(JpaServiceTableRepository serviceTableRepository) {
        this.serviceTableRepository = serviceTableRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(ServiceTableActiveQuery query) {
        Objects.requireNonNull(query, "Service table active query is required");
        
        var serviceTables = serviceTableRepository.findAllByActiveFetchArea(query.getActive());

        List<ServiceTableSummaryResponse> responses = serviceTables.stream()
                .map(e -> {
                    var serviceTableResponse = ServiceTableSummaryResponse.builder()
                            .id(e.getId())
                            .isActive(e.getActive())
                            .name(e.getTableNumber().getValue())
                            .areaId(e.getArea().map(Area::getId).orElse(null))
                            .build();
                    
                    e.getArea().ifPresent(area -> {
                        serviceTableResponse.setAreaId(area.getId());
                    });
                    
                    return serviceTableResponse;
                }).toList();
        
        return CommandResult.success(responses);
    }
}
