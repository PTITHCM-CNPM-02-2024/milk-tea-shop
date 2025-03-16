package com.mts.backend.application.staff.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class ManagerDetailResponse extends UserDetailResponse{
    private Long accountId;
}
