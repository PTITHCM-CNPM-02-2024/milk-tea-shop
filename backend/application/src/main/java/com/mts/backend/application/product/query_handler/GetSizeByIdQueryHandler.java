package com.mts.backend.application.product.query_handler;

import com.mts.backend.application.product.query.SizeByIdQuery;
import com.mts.backend.application.product.response.SizeDetailResponse;
import com.mts.backend.application.product.response.UnitDetailResponse;
import com.mts.backend.domain.product.jpa.JpaProductSizeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetSizeByIdQueryHandler implements IQueryHandler<SizeByIdQuery, CommandResult> {
    private final JpaProductSizeRepository sizeRepository;

    public GetSizeByIdQueryHandler(JpaProductSizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    @Override
    public CommandResult handle(SizeByIdQuery query) {
        Objects.requireNonNull(query, "SizeByIdQuery is required");

        var size = sizeRepository.findById(query.getId()).orElseThrow(() -> new NotFoundException("Không tìm thấy kích cỡ"));

        var sizeResponse = SizeDetailResponse.builder()
                .id(size.getId())
                .name(size.getName().getValue())
                .unit(UnitDetailResponse.builder()
                        .id(size.getUnit().getId())
                        .name(size.getUnit().getName().getValue())
                        .build())
                .quantity(size.getQuantity().getValue())
                .build();

        return CommandResult.success(sizeResponse);
    }
}