package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.DefaultUnitQuery;
import com.mts.backend.application.product.response.UnitDetailResponse;
import com.mts.backend.domain.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllUnitQueryHandler implements IQueryHandler<DefaultUnitQuery, CommandResult> {
    
    private final JpaUnitOfMeasureRepository unitRepository;
    
    public GetAllUnitQueryHandler(JpaUnitOfMeasureRepository unitRepository) {
        this.unitRepository = unitRepository;
    }
    
    @Override
    public CommandResult handle(DefaultUnitQuery query) {
        Objects.requireNonNull(query, "DefaultUnitQuery is required");
        
        var units = unitRepository.findAll();
        
        List<UnitDetailResponse> responses = units.stream().map(unit -> {
            return UnitDetailResponse.builder()
                    .id(unit.getId())
                    .name(unit.getName().getValue())
                    .symbol(unit.getSymbol().getValue())
                    .description(unit.getDescription().orElse(null))
                    .build();
        }).toList();
        
        return CommandResult.success(responses);
    }
}
