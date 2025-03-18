package com.mts.backend.api.staff.request;


import com.mts.backend.api.common.request.UserBaseRequest;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class CreateManagerRequest extends UserBaseRequest {
}
