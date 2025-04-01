package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.DefaultServiceTableQuery;
import com.mts.backend.application.store.response.ServiceTableDetailResponse;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllServiceTableQueryHandler implements IQueryHandler<DefaultServiceTableQuery, CommandResult>
{
    private final JpaServiceTableRepository serviceTableRepository;
    
    public GetAllServiceTableQueryHandler(JpaServiceTableRepository serviceTableRepository)
    {
        this.serviceTableRepository = serviceTableRepository;
    }

    /**
     * @param query 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(DefaultServiceTableQuery query) {
        Objects.requireNonNull(query, "DefaultServiceTableQuery is required");
        
        var serviceTables = serviceTableRepository.findAllWithArea(Pageable.ofSize(query.getSize()));
        
        List<ServiceTableDetailResponse> responses = new ArrayList<>();
        
        serviceTables.forEach(se -> {
            var response = ServiceTableDetailResponse.builder()
                    .id(se.getId())
                    .isActive(se.getActive())
                    .areaId(se.getAreaEntity().map(a -> a.getId()).orElse(null))
                    .name(se.getTableNumber().getValue())
                    .build();
                    
            responses.add(response);
        }
        
        );
        
        return CommandResult.success(responses);
    }
    

}
