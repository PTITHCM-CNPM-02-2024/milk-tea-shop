package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.AreaByIdQuery;
import com.mts.backend.application.store.response.AreaDetailResponse;
import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.AreaEntity;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.domain.store.repository.IAreaRepository;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetAreaByIdQueryHandler implements IQueryHandler<AreaByIdQuery, CommandResult> {
    
    private final JpaAreaRepository areaRepository;
    
    public GetAreaByIdQueryHandler(JpaAreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(AreaByIdQuery query) {
        Objects.requireNonNull(query,"GetAreaByIdQuery is required");
        
        var area = mustExistArea(query.getId());

        AreaDetailResponse response = AreaDetailResponse.builder().id(area.getId().getValue())
                .name(area.getName().getValue())
                .description(area.getDescription().orElse(null))
                .maxTable(area.getMaxTable().map(MaxTable::getValue).orElse(null))
                .isActive(area.getActive())
                .build();
        
        return CommandResult.success(response);
    }
    
    private AreaEntity mustExistArea(AreaId areaId) {
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new NotFoundException("Khu vực không tồn tại"));
    }
}
