package com.mts.backend.application.payment.response;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Value
@Data
public class PaymentInitResponse {
    @NotNull
    String message;
    @NotNull
    Long paymentId;
    @NotNull
    PaymentInitStatus status;
    @Nullable
    String redirectUrl;
    @Nullable
    BigDecimal amount;
    @Nullable
    Map<String, String> additionalData;
    public PaymentInitResponse(Long payment, PaymentInitStatus status, String message, BigDecimal amount,
                               String redirectUrl,
                               Map<String, String> additionalData) {
        this.paymentId = Objects.requireNonNull(payment, "payment is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.redirectUrl = redirectUrl;
        this.amount = Objects.requireNonNull(amount, "amount is required");
        this.message = message != null ? message : "";
        this.additionalData = additionalData != null ? Collections.unmodifiableMap(additionalData) : Collections.emptyMap();
    }

    public PaymentInitResponse(Long payment, PaymentInitStatus status, String message, BigDecimal amount,
                               String redirectUrl) {
        this(payment, status, message, amount, redirectUrl, null);
    }

    public static PaymentInitResponse success(Long payment, String message, BigDecimal amount) {
        return new PaymentInitResponse(payment, PaymentInitStatus.SUCCESS, message, amount, null);
    }

    public static PaymentInitResponse redirect(Long payment, String message, BigDecimal amount, String redirectUrl) {
        return new PaymentInitResponse(payment, PaymentInitStatus.REDIRECT, message, amount, redirectUrl);
    }

    public static PaymentInitResponse processing(Long payment, String message, BigDecimal amount) {
        return new PaymentInitResponse(payment, PaymentInitStatus.PROCESSING, message, amount, null);
    }

    public static PaymentInitResponse failed(Long payment, String message) {
        return new PaymentInitResponse(payment, PaymentInitStatus.FAILED, message, null, null);
    }

    public boolean isSuccess() {
        return status == PaymentInitStatus.SUCCESS;
    }

    public boolean isRedirect() {
        return status == PaymentInitStatus.REDIRECT;
    }

    public boolean isProcessing() {
        return status == PaymentInitStatus.PROCESSING;
    }

    public boolean isFailed() {
        return status == PaymentInitStatus.FAILED;
    }

    public Optional<String> getRedirectUrl() {
        return Optional.ofNullable(redirectUrl);
    }

    public Map<String, String> getAdditionalData() {
        return additionalData != null ? Collections.unmodifiableMap(additionalData) : Collections.emptyMap();
    }

    public enum PaymentInitStatus {
        SUCCESS,
        REDIRECT,
        PROCESSING,
        FAILED
    }
}
