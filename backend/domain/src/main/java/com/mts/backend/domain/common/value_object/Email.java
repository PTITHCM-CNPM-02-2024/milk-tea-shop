package com.mts.backend.domain.common.value_object;

import com.mts.backend.shared.exception.DomainBusinessLogicException;
import jakarta.persistence.AttributeConverter;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Value(staticConstructor = "of")
public class Email {
    @Size(max = 100, message = "Email không được vượt quá 255 ký tự")
    @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Email không hợp lệ")
    String value;
}
