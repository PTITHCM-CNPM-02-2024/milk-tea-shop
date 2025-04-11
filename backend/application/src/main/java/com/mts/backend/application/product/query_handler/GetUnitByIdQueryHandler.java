package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.UnitByIdQuery;
import com.mts.backend.application.product.response.UnitDetailResponse;
import com.mts.backend.domain.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetUnitByIdQueryHandler implements IQueryHandler<UnitByIdQuery, CommandResult> {
    
    private final JpaUnitOfMeasureRepository unitRepository;
    
    public GetUnitByIdQueryHandler(JpaUnitOfMeasureRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(UnitByIdQuery query) {
        Objects.requireNonNull(query, "UnitByIdQuery is required");
        
        var unit = unitRepository.findById(query.getId().getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy đơn vị đo lường"));
        
        var response = UnitDetailResponse.builder()
                .id(unit.getId())
                .symbol(unit.getSymbol().getValue())
                .name(unit.getName().getValue())
                .build();
        
        return CommandResult.success(response);
    }
}
