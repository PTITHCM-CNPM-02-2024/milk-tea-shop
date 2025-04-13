package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.ServiceTableByAreaIdQuery;
import com.mts.backend.application.store.response.ServiceTableSummaryResponse;
import com.mts.backend.domain.store.AreaEntity;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetAllTblByAreaIdQueryHandler implements IQueryHandler<ServiceTableByAreaIdQuery, CommandResult> {
    private final JpaServiceTableRepository jpaServiceTableRepository;

    public GetAllTblByAreaIdQueryHandler(JpaServiceTableRepository jpaServiceTableRepository) {
        this.jpaServiceTableRepository = jpaServiceTableRepository;
    }

    /**
     * @param query
     * @return
     */
    @Override
    public CommandResult handle(ServiceTableByAreaIdQuery query) {
        Objects.requireNonNull(query, "ServiceTableByAreaIdQuery is required");


        var serviceTables = jpaServiceTableRepository.findByAreaEntity_Id(query.getAreaId().map(AreaId::getValue).orElse(null), Pageable.ofSize(query.getSize()).withPage(query.getPage()));
        
        Page<ServiceTableSummaryResponse> responses = serviceTables.map(se -> {
            var response = ServiceTableSummaryResponse.builder()
                    .id(se.getId())
                    .name(se.getTableNumber().getValue())
                    .isActive(se.getActive())
                    .areaId(se.getAreaEntity().map(AreaEntity::getId).orElse(null))
                    .build();
            return response;
        });
        
        return CommandResult.success(responses);
    }
}
