package com.mts.backend.application.report.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class CatRevenueReportQuery implements IQuery<CommandResult> {
    private String name;
    private String id;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    public Optional<LocalDateTime> getFromDate() {
        return Optional.ofNullable(fromDate);
    }

    public Optional<LocalDateTime> getToDate() {
        return Optional.ofNullable(toDate);
    }

}
