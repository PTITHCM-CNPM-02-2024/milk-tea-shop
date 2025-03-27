package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.AreaActiveQuery;
import com.mts.backend.application.store.response.AreaDetailResponse;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Service
public class GetAllAreaActiveQueryHandler implements IQueryHandler<AreaActiveQuery, CommandResult> {
    private final JpaAreaRepository areaRepository;
    
    public GetAllAreaActiveQueryHandler(JpaAreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(AreaActiveQuery query) {
        Objects.requireNonNull(query, "Area active query is required");
        var areas = areaRepository.findByActive(query.getActive(), Pageable.ofSize(query.getSize()));
        
        List<AreaDetailResponse> result = areas.stream()
                .map(area -> AreaDetailResponse.builder()
                        .id(area.getId().getValue())
                        .name(area.getName().getValue())
                        .maxTable(area.getMaxTable().map(MaxTable::getValue).orElse(null))
                        .isActive(area.getActive())
                        .description(area.getDescription().orElse(null))
                        .build()).toList();
        
        return CommandResult.success(result);
    }
}
