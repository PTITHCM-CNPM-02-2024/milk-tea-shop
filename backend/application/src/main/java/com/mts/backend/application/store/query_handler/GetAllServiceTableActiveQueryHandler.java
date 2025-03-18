package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.ServiceTableActiveQuery;
import com.mts.backend.application.store.response.ServiceTableDetailResponse;
import com.mts.backend.domain.store.repository.IServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GetAllServiceTableActiveQueryHandler implements IQueryHandler<ServiceTableActiveQuery, CommandResult> {
    private final IServiceTableRepository serviceTableRepository;
    
    public GetAllServiceTableActiveQueryHandler(IServiceTableRepository serviceTableRepository) {
        this.serviceTableRepository = serviceTableRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(ServiceTableActiveQuery query) {
        Objects.requireNonNull(query, "Service table active query is required");
        
        var serviceTables = serviceTableRepository.findAllActive();

        List<ServiceTableDetailResponse> responses = serviceTables.stream()
                .map(e -> ServiceTableDetailResponse.builder()
                        .id(e.getId().getValue())
                        .name(e.getTableNumber().getValue())
                        .areaId(e.getAreaId().isPresent() ? e.getAreaId().get().getValue() : null)
                        .isActive(e.isActive())
                        .build()).toList();
        
        return CommandResult.success(responses);
    }
}
