package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.ServiceTableActiveQuery;
import com.mts.backend.application.store.response.AreaDetailResponse;
import com.mts.backend.application.store.response.ServiceTableDetailResponse;
import com.mts.backend.application.store.response.ServiceTableSummaryResponse;
import com.mts.backend.domain.store.AreaEntity;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
        
        var serviceTables = serviceTableRepository.findAllByActiveFetchArea(query.getActive(),
                query.getAreaId().map(AreaId::getValue).orElse(null), Pageable.ofSize(query.getSize()).withPage(query.getPage()));

        Slice<ServiceTableSummaryResponse> responses = serviceTables
                .map(e -> ServiceTableSummaryResponse.builder()
                        .id(e.getId())
                        .name(e.getTableNumber().getValue())
                        .areaId(e.getAreaEntity().map(AreaEntity::getId).orElse(null))
                        .isActive(e.getActive())
                        .build());
        
        return CommandResult.success(responses);
    }
}
