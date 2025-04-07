package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.DefaultAreaQuery;
import com.mts.backend.application.store.response.AreaDetailResponse;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllAreaQueryHandler implements IQueryHandler<DefaultAreaQuery, CommandResult> {
    private final JpaAreaRepository areaRepository;
    
    public GetAllAreaQueryHandler(JpaAreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(DefaultAreaQuery query) {
        Objects.requireNonNull(query,"DefaultAreaQuery is required");
        
        var areas = areaRepository.findAll().stream().filter(area -> {
            if (query.getActive().isPresent()){
                return area.getActive() == query.getActive().get();
            }
            
            return true;
        }).toList();
        
        List<AreaDetailResponse> responses = new ArrayList<>();
        
        areas.forEach(area -> {
            var response = AreaDetailResponse.builder()
                    .id(area.getId())
                    .name(area.getName().getValue())
                    .description(area.getDescription().orElse(null))
                    .maxTable(area.getMaxTable().map(MaxTable::getValue).orElse(null))
                    .isActive(area.getActive())
                    .build();
            
            responses.add(response);
        });
        
        return CommandResult.success(responses);
    }
}
