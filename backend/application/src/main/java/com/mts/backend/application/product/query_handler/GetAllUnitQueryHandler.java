package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.DefaultUnitQuery;
import com.mts.backend.application.product.response.UnitDetailResponse;
import com.mts.backend.domain.product.jpa.JpaUnitOfMeasureRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        
        var units = unitRepository.findAll(Pageable.ofSize(query.getSize()).withPage(query.getPage()));
        
        Page<UnitDetailResponse> responses = units.map(unit -> {
            return UnitDetailResponse.builder()
                    .id(unit.getId())
                    .symbol(unit.getSymbol().getValue())
                    .name(unit.getName().getValue())
                    .description(unit.getDescription().orElse(null))
                    .build();
        });
        
        return CommandResult.success(responses);
    }
}
