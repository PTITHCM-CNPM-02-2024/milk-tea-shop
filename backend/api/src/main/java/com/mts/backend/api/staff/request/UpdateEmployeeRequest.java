package com.mts.backend.api.staff.request;

import com.mts.backend.api.common.request.UserBaseRequest;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class UpdateEmployeeRequest extends UserBaseRequest {
    private Long id;
    private String position;
}
