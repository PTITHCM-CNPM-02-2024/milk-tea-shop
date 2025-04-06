package com.mts.backend.application.staff.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = false)
public class EmployeeDetailResponse extends UserDetailResponse{
    private String position;
    private Long accountId;
}
