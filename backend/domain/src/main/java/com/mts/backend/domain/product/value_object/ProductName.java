package com.mts.backend.domain.product.value_object;

import jakarta.validation.constraints.Size;
import lombok.Value;

import java.util.Locale;

@Value(staticConstructor = "of")
public class ProductName {
    private static final int MAX_LENGTH = 100;
    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Tên không được để trống")
    String value;

    public ProductName(@jakarta.validation.constraints.NotBlank(message = "Tên không được để trống") @Size(max = 100, message = "Tên không được vượt quá 100 ký tự") String value) {

        this.value = normalize(value);
    }

    /**
     * Convert to uppercase and trim
     *
     * @param value
     * @return
     */
    private static String normalize(String value) {
        return value.trim().toUpperCase(Locale.of("vi", "VN"));
    }
    

}
