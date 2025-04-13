package com.mts.backend.application.store.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StoreDetailResponse {
    
    private Integer id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String taxCode;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalDate openingDate;
    
}
