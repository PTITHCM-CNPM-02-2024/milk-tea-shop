package com.mts.backend.api.customer.request;

import com.mts.backend.api.common.request.UserBaseRequest;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateCustomerRequest extends UserBaseRequest {
    private Long id;
    private Integer memberId;
}
