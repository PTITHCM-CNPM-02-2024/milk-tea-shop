package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.AreaActiveQuery;
import com.mts.backend.application.store.response.AreaDetailResponse;
import com.mts.backend.application.store.response.AreaSummaryResponse;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
        
        Slice<AreaSummaryResponse> result = areas.map(area -> {
            return AreaSummaryResponse.builder()
                    .id(area.getId())
                    .name(area.getName().getValue())
                    .description(area.getDescription().orElse(null))
                    .maxTable(area.getMaxTable().map(MaxTable::getValue).orElse(null))
                    .isActive(area.getActive())
                    .build();
        });
        
        return CommandResult.success(result);
    }
}
