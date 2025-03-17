package com.mts.backend.api.customer.request;

import com.mts.backend.api.common.request.UserBaseRequest;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class CreateCustomerRequest extends UserBaseRequest {
    private Integer memberId;
}
