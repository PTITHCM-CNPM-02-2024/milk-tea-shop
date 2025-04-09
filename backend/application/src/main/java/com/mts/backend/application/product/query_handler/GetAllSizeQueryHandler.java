package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.DefaultSizeQuery;
import com.mts.backend.application.product.response.SizeDetailResponse;
import com.mts.backend.application.product.response.SizeSummaryResponse;
import com.mts.backend.application.product.response.UnitDetailResponse;
import com.mts.backend.domain.product.jpa.JpaProductSizeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GetAllSizeQueryHandler implements IQueryHandler<DefaultSizeQuery, CommandResult> {

    private final JpaProductSizeRepository sizeRepository;

    public GetAllSizeQueryHandler(JpaProductSizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    @Override
    public CommandResult handle(DefaultSizeQuery query) {
        Objects.requireNonNull(query, "DefaultSizeQuery is required");

        var sizes = sizeRepository.findAllWithJoinFetch(Pageable.ofSize(query.getSize()).withPage(query.getPage()));

        Page<SizeSummaryResponse> sizeDetailResponses = sizes.map(size -> {
            return SizeSummaryResponse.builder()
                    .id(size.getId())
                    .name(size.getName().getValue())
                    .unitId(size.getUnit().getId())
                    .quantity(size.getQuantity().getValue())
                    .build();
        });
        
        return CommandResult.success(sizeDetailResponses);
    }
}
