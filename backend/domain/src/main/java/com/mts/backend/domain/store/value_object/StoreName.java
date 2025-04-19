package com.mts.backend.domain.store.value_object;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value(staticConstructor = "of")
public  class StoreName {

    @Size(max = 100, message = "Tên cửa hàng không được vượt quá 100 ký tự")
    @NotBlank(message = "Tên cửa hàng không được để trống")
    String value;

    private StoreName(@Size(max = 100, message = "Tên cửa hàng không được vượt quá 100 ký tự") @NotBlank(message = "Tên cửa hàng không được để trống") String value) {
        this.value = normalize(value);
    }


    private static String normalize(String value) {
        return value.trim().toUpperCase();
    }
}
