package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.AreaActiveQuery;
import com.mts.backend.application.store.response.AreaDetailResponse;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
public class GetAllAreaActiveQueryHandler implements IQueryHandler<AreaActiveQuery, CommandResult> {
    private final IAreaRepository areaRepository;
    
    public GetAllAreaActiveQueryHandler(IAreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(AreaActiveQuery query) {
        Objects.requireNonNull(query, "Area active query is required");
        var areas = areaRepository.findAllActive();
        
        List<AreaDetailResponse> result = areas.stream()
                .map(area -> AreaDetailResponse.builder()
                        .id(area.getId().getValue())
                        .name(area.getAreaName().getValue())
                        .maxTable(area.getMaxTable().map(MaxTable::getValue).orElse(null))
                        .isActive(area.isActive())
                        .description(area.getDescription().orElse(null))
                        .build()).toList();
        
        return CommandResult.success(result);
    }
}
