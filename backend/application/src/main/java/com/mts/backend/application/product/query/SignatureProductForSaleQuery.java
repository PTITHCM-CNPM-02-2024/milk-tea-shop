package com.mts.backend.application.product.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class SignatureProductForSaleQuery extends ProductForSaleQuery {
    private boolean isSignature;
}
