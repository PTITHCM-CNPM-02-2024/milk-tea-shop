package com.mts.backend.application.store.response;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper =false)
public class AreaDetailResponse extends AreaSummaryResponse{
    private List<ServiceTableSummaryResponse> areas;
}
