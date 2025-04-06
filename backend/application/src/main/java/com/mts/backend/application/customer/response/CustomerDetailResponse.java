package com.mts.backend.application.customer.response;

import com.mts.backend.application.staff.response.UserDetailResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CustomerDetailResponse extends UserDetailResponse {
    private Integer rewardPoint;
    private Integer membershipId;
    private Long accountId;
}
