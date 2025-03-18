package com.mts.backend.application.staff.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class EmployeeDetailResponse extends UserDetailResponse{
    private String position;
    private Long accountId;
}
