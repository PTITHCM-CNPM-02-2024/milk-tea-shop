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
public class  DefaultAreaQuery  implements IQuery<CommandResult> {
    private Boolean active;
    
    public Optional<Boolean> getActive() {
        return Optional.ofNullable(active);
    }
}
