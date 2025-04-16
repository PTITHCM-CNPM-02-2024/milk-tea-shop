package com.mts.backend.application.staff.query;

import com.mts.backend.domain.staff.identifier.EmployeeId;
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
@Data
@Builder
public class OrderRevenueByTimeAndEmpIdQuery implements IQuery<CommandResult> {
    private EmployeeId id;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    
    public Optional<LocalDateTime> getFromDate() {
        return Optional.ofNullable(fromDate);
    }
    
    public Optional<LocalDateTime> getToDate() {
        return Optional.ofNullable(toDate);
    }
}
