package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.DefaultSizeQuery;
import com.mts.backend.application.product.response.SizeDetailResponse;
import com.mts.backend.application.product.response.UnitDetailResponse;
import com.mts.backend.domain.product.jpa.JpaProductSizeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllSizeQueryHandler implements IQueryHandler<DefaultSizeQuery, CommandResult> {

    private final JpaProductSizeRepository sizeRepository;

    public GetAllSizeQueryHandler(JpaProductSizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(DefaultSizeQuery query) {
        Objects.requireNonNull(query, "DefaultSizeQuery is required");

        var sizes = sizeRepository.findAllWithJoinFetch();

        List<SizeDetailResponse> sizeDetailResponses = sizes.stream().map(
                size -> SizeDetailResponse.builder()
                        .id(size.getId())
                        .name(size.getName().getValue())
                        .unit(UnitDetailResponse.builder()
                                .id(size.getUnit().getId())
                                .symbol(size.getUnit().getSymbol().getValue())
                                .name(size.getUnit().getName().getValue())
                                .build())
                        .quantity(size.getQuantity().getValue())
                        .build()).toList();
        
        return CommandResult.success(sizeDetailResponses);
    }
}
