package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.ServiceTableActiveQuery;
import com.mts.backend.application.store.response.AreaDetailResponse;
import com.mts.backend.application.store.response.ServiceTableDetailResponse;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.domain.store.value_object.MaxTable;
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

        List<ServiceTableDetailResponse> responses = serviceTables.stream()
                .map(e -> ServiceTableDetailResponse.builder()
                        .id(e.getId())
                        .name(e.getTableNumber().getValue())
                        .area(e.getAreaEntity().map(area -> AreaDetailResponse.builder()
                                .id(area.getId())
                                .name(area.getName().getValue())
                                .maxTable(area.getMaxTable().map(MaxTable::getValue).orElse(null))
                                .isActive(area.getActive())
                                .description(area.getDescription().orElse(null))
                                .build()).orElse(null))
                        .isActive(e.getActive())
                        .build()).toList();
        
        return CommandResult.success(responses);
    }
}
