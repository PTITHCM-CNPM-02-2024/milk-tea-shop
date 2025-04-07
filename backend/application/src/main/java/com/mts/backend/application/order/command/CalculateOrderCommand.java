package com.mts.backend.application.order.command;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
@SuperBuilder
public class CalculateOrderCommand extends CreateOrderCommand{
    
}
