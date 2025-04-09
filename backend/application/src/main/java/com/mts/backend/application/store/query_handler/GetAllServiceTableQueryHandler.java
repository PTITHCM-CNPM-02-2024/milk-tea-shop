package com.mts.backend.application.store.query_handler;

import com.mts.backend.application.store.query.DefaultServiceTableQuery;
import com.mts.backend.application.store.response.AreaDetailResponse;
import com.mts.backend.application.store.response.ServiceTableDetailResponse;
import com.mts.backend.application.store.response.ServiceTableSummaryResponse;
import com.mts.backend.domain.store.jpa.JpaServiceTableRepository;
import com.mts.backend.domain.store.value_object.MaxTable;
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
            return ServiceTableDetailResponse.builder()
                    .id(se.getId())
                    .isActive(se.getActive())
                    .area(se.getAreaEntity().map(area -> AreaDetailResponse.builder()
                            .id(area.getId())
                            .name(area.getName().getValue())
                            .maxTable(area.getMaxTable().map(MaxTable::getValue).orElse(null))
                            .isActive(area.getActive())
                            .description(area.getDescription().orElse(null))
                            .build()).orElse(null))
                    .name(se.getTableNumber().getValue())
                    .build();
                });

        return CommandResult.success(responses);
    }


}
