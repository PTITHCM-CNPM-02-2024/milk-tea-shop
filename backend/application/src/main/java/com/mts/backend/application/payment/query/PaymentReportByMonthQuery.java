package com.mts.backend.application.payment.query;

import java.time.LocalDate;
import java.util.Optional;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaymentReportByMonthQuery implements IQuery<CommandResult> {
    private Integer year;
    private Integer month;

    public Integer getYear() {
        if (year == null) { 
            return LocalDate.now().getYear();
        }
        return year;
    }

    public Integer getMonth() {
        if (month == null) {
            return LocalDate.now().getMonthValue();
        }
        return month;
    }
    
}