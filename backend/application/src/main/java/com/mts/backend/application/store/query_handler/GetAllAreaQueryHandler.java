package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.DefaultAreaQuery;
import com.mts.backend.application.store.response.AreaDetailResponse;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllAreaQueryHandler implements IQueryHandler<DefaultAreaQuery, CommandResult> {
    private final IAreaRepository areaRepository;
    
    public GetAllAreaQueryHandler(IAreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(DefaultAreaQuery query) {
        Objects.requireNonNull(query,"DefaultAreaQuery is required");
        
        var areas = areaRepository.findAll();
        
        List<AreaDetailResponse> responses = new ArrayList<>();
        
        areas.forEach(area -> {
            var response = AreaDetailResponse.builder()
                    .id(area.getId().getValue())
                    .name(area.getAreaName().getValue())
                    .description(area.getDescription().orElse(null))
                    .maxTable(area.getMaxTable().map(MaxTable::getValue).orElse(null))
                    .isActive(area.isActive())
                    .build();
            
            responses.add(response);
        });
        
        return CommandResult.success(responses);
    }
}
