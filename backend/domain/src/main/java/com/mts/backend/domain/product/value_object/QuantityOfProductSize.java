package com.mts.backend.domain.product.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
@Value(staticConstructor = "of")
public class QuantityOfProductSize {
    @PositiveOrZero(message = "Số lượng không được âm")
    int value;
    
}
