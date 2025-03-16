package com.mts.backend.api.staff.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class UpdateManagerRequest extends UserBaseRequest {
    private Long id;
}
