package com.mts.backend.api.store.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StoreBaseRequest {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String taxCode;
    private String openTime;
    private String closeTime;
    private String openingDate;
}
