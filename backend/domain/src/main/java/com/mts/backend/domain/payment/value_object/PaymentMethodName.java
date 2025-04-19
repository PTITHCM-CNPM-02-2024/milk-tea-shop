package com.mts.backend.domain.payment.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Value(staticConstructor = "of")
public class PaymentMethodName {
    private static final int MAX_LENGTH = 50;
    @jakarta.validation.constraints.Size(max = 50, message = "Tên phương thức thanh toán không được vượt quá 50 ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Tên phương thức thanh toán không được để trống")
    String value;

    public PaymentMethodName(@jakarta.validation.constraints.NotBlank(message = "Tên phương thức thanh toán không được để trống") @jakarta.validation.constraints.Size(max = 50, message = "Tên phương thức thanh toán không được vượt quá 50 ký tự") String value) {

        this.value = normalize(value);
    }


    private static String normalize(String value) {
        return value.trim().toUpperCase();
    }

}
