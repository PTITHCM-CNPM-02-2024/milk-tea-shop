package com.mts.backend.application.store.query;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class DefaultServiceTableQuery implements IQuery<CommandResult> {
    private Integer size;
    private Integer page;
    private Boolean active;
    
    public Optional<Boolean> getActive() {
        return Optional.ofNullable(active);
    }
}
