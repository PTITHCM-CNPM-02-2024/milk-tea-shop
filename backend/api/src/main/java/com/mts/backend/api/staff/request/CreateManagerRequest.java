package com.mts.backend.api.staff.request;


import com.mts.backend.api.common.request.UserBaseRequest;
import lombok.*;

@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class CreateManagerRequest extends UserBaseRequest {
}
