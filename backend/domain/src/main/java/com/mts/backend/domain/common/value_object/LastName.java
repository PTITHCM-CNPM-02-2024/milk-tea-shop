package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Value(staticConstructor = "of")
public class LastName {


    private final static Pattern VIETNAMESE_NAME_PATTERN = Pattern.compile("^[A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*(?:[ ][A-ZÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ][a-zàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ]*)*$");
    @jakarta.validation.constraints.Size(max = 70, message = "Họ không được vượt quá 70 ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Họ không được để trống")
    String value;

    public LastName(@jakarta.validation.constraints.Size(max = 70, message = "Họ không được vượt quá 70 ký tự") @jakarta.validation.constraints.NotBlank(message = "Họ không được để trống") String value) {
        this.value = normalize(value);
    }

    /**
     * @param value
     * @return
     */
    private static String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ").toUpperCase();
    }
}
