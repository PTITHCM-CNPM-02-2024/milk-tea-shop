package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.DefaultUnitQuery;
import com.mts.backend.application.product.response.UnitDetailResponse;
import com.mts.backend.domain.product.repository.IUnitRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllUnitQueryHandler implements IQueryHandler<DefaultUnitQuery, CommandResult> {
    
    private final IUnitRepository unitRepository;
    
    public GetAllUnitQueryHandler(IUnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }
    
    @Override
    public CommandResult handle(DefaultUnitQuery query) {
        Objects.requireNonNull(query, "DefaultUnitQuery is required");
        
        var units = unitRepository.findAll();
        
        List<UnitDetailResponse> responses = new ArrayList<>();
        
        units.forEach(unit -> {
            UnitDetailResponse response = UnitDetailResponse.builder().id(unit.getId().getValue()).name(unit.getName().getValue()).symbol(unit.getSymbol().getValue()).description(unit.getDescription().orElse("")).build();
            
            responses.add(response);
        });
        
        return CommandResult.success(responses);
    }
}
