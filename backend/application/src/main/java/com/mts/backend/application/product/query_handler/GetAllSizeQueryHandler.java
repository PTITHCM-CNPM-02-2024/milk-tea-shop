package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.DefaultSizeQuery;
import com.mts.backend.application.product.response.SizeDetailResponse;
import com.mts.backend.application.product.response.UnitDetailResponse;
import com.mts.backend.domain.product.repository.ISizeRepository;
import com.mts.backend.domain.product.repository.IUnitRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllSizeQueryHandler implements IQueryHandler<DefaultSizeQuery, CommandResult> {

    private ISizeRepository sizeRepository;

    private IUnitRepository unitRepository;

    public GetAllSizeQueryHandler(ISizeRepository sizeRepository, IUnitRepository unitRepository) {
        this.sizeRepository = sizeRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    public CommandResult handle(DefaultSizeQuery query) {
        Objects.requireNonNull(query, "DefaultSizeQuery is required");

        var sizes = sizeRepository.findAll();

        List<SizeDetailResponse> sizeDetailResponses = new ArrayList<>();

        sizes.forEach(size -> {
            var unit = unitRepository.findById(size.getUnitOfMeasure()).orElseThrow(() -> new RuntimeException("Unit not found"));
            sizeDetailResponses.add(SizeDetailResponse.builder()
                    .id(size.getId().getValue())
                    .name(size.getName().getValue())
                    .unit(UnitDetailResponse.builder()
                            .id(unit.getId().getValue())
                            .name(unit.getName().getValue())
                            .build())
                    .quantity(size.getQuantity().getValue())
                    .build());
        });
        
        return CommandResult.success(sizeDetailResponses);
    }
}
