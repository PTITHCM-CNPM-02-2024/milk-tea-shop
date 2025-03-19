package com.mts.backend.api.store.request;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class UpdateStoreRequest extends StoreBaseRequest {
}
