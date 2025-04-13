package com.mts.backend.application.order.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderTableDetailResponse {
    private String tableNumber;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
}
