package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.DefaultServiceTableQuery;
import com.mts.backend.application.store.response.ServiceTableSummaryResponse;
import com.mts.backend.domain.store.Area;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetAllServiceTableQueryHandler implements IQueryHandler<DefaultServiceTableQuery, CommandResult> {
    private final JpaServiceTableRepository serviceTableRepository;

    public GetAllServiceTableQueryHandler(JpaServiceTableRepository serviceTableRepository) {
        this.serviceTableRepository = serviceTableRepository;
    }

    /**
     * @param query
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(DefaultServiceTableQuery query) {
        Objects.requireNonNull(query, "DefaultServiceTableQuery is required");

        var serviceTables =
                serviceTableRepository.findAllFetchArea(Pageable.ofSize(query.getSize()).withPage(query.getPage()));


        Page<ServiceTableSummaryResponse> responses = serviceTables.map(se -> {
            var response = ServiceTableSummaryResponse.builder()
                    .id(se.getId())
                    .name(se.getTableNumber().getValue())
                    .isActive(se.getActive())
                    .areaId(se.getArea().map(Area::getId).orElse(null))
                    .build();
            return response;
        });

        return CommandResult.success(responses);
    }


}
