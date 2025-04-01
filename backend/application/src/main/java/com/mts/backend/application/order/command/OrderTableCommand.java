package com.mts.backend.application.order.command;

import com.mts.backend.domain.store.identifier.ServiceTableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderTableCommand {
    
    private ServiceTableId serviceTableId;
}
