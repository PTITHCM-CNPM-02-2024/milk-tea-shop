package com.mts.backend.domain.common.value_object;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.regex.Pattern;

@Value(staticConstructor = "of")
public class FirstName {


    private final static Pattern VIETNAMESE_NAME_PATTERN = Pattern.compile("^[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*(?:[ ][A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*)*$");
    @jakarta.validation.constraints.Size(max = 70, message = "Tên không được vượt quá 70 ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Tên không được để trống")
    String value;

    public FirstName(@jakarta.validation.constraints.Size(max = 70, message = "Tên không được vượt quá 70 ký tự") @jakarta.validation.constraints.NotBlank(message = "Tên không được để trống") String value) {
        this.value = normalize(value);
    }


    private String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ").toUpperCase();
    }

}
