package com.mts.backend.application.account.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RoleDetailResponse {
    private Integer id;
    private String name;
    private String description;
}
