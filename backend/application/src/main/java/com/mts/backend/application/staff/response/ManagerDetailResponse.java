package com.mts.backend.application.staff.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class ManagerDetailResponse extends UserDetailResponse{
    private Long accountId;
}
