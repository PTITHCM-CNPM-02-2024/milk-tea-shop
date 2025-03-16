package com.mts.backend.api.staff.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class CreateEmployeeRequest extends UserBaseRequest{
    private String position;
}
