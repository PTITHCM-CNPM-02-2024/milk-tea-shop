package com.mts.backend.application.promotion.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import com.mts.backend.shared.query.IQueryHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Builder
@Data
public class DefaultDiscountQuery implements IQuery<CommandResult>{
    private Integer size;
    private Integer page;
}
