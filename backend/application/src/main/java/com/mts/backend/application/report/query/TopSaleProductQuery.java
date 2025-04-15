package com.mts.backend.application.report.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import com.mts.backend.shared.query.IQueryHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TopSaleProductQuery implements IQuery<CommandResult> {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    @Builder.Default
    private int limit = 10;
    
    public Optional<LocalDateTime> getFromDate() {
        return Optional.ofNullable(fromDate);
    }
    
    public Optional<LocalDateTime> getToDate() {
        return Optional.ofNullable(toDate);
    }
}
