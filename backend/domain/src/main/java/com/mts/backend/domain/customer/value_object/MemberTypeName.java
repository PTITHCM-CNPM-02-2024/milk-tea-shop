package com.mts.backend.domain.customer.value_object;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value(staticConstructor = "of")
public class MemberTypeName {
    private static final int MAX_LENGTH = 50;
    @Size(max = 50, message = "Tên loại thành viên không được vượt quá 50 ký tự")
    @NotBlank(message = "Tên loại thành viên không được để trống")
    String value;

    public MemberTypeName(@NotBlank(message = "Tên loại thành viên không được để trống") @Size(max = 50, message = "Tên loại thành viên không được vượt quá 50 ký tự") String value) {
        this.value = normalize(value);
    }


    private static String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ").toUpperCase();
    }


}
