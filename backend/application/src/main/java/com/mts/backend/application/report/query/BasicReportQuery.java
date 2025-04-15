package com.mts.backend.application.report.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
@Data
public class BasicReportQuery implements IQuery<CommandResult> {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    public Optional<LocalDateTime> getFromDate() {
        return Optional.ofNullable(fromDate);
    }

    public Optional<LocalDateTime> getToDate() {
        return Optional.ofNullable(toDate);
    }
}
