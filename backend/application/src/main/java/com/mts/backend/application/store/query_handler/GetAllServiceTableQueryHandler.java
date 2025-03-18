package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.DefaultServiceTableQuery;
import com.mts.backend.application.store.response.ServiceTableDetailResponse;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.repository.IServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllServiceTableQueryHandler implements IQueryHandler<DefaultServiceTableQuery, CommandResult>
{
    private final IServiceTableRepository serviceTableRepository;
    
    public GetAllServiceTableQueryHandler(IServiceTableRepository serviceTableRepository)
    {
        this.serviceTableRepository = serviceTableRepository;
    }

    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(DefaultServiceTableQuery query) {
        Objects.requireNonNull(query, "DefaultServiceTableQuery is required");
        
        var serviceTables = serviceTableRepository.findAll();
        
        List<ServiceTableDetailResponse> responses = new ArrayList<>();
        
        serviceTables.forEach(se -> {
            var response = ServiceTableDetailResponse.builder()
                    .id(se.getId().getValue())
                    .isActive(se.isActive())
                    .areaId(se.getAreaId().map(AreaId::getValue).orElse(null))
                    .name(se.getTableNumber().getValue())
                    .build();
                    
            responses.add(response);
        }
        
        );
        
        return CommandResult.success(responses);
    }
    

}
