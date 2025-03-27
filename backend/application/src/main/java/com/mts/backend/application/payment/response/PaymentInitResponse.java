package com.mts.backend.application.payment.response;

import com.mts.backend.domain.promotion.identifier.CouponId;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Value
@Data
public class PaymentInitResponse {

    String message;
    CouponId paymentId;
    PaymentInitStatus status;
    String redirectUrl;
    BigDecimal amount;
    Map<String, String> additionalData;
    public PaymentInitResponse(CouponId payment, PaymentInitStatus status, String message, BigDecimal amount,
                               String redirectUrl,
                               Map<String, String> additionalData) {
        this.paymentId = Objects.requireNonNull(payment, "payment is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.redirectUrl = redirectUrl;
        this.amount = Objects.requireNonNull(amount, "amount is required");
        this.message = message != null ? message : "";
        this.additionalData = additionalData != null ? Collections.unmodifiableMap(additionalData) : Collections.emptyMap();
    }

    public PaymentInitResponse(CouponId payment, PaymentInitStatus status, String message, BigDecimal amount,
                               String redirectUrl) {
        this(payment, status, message, amount, redirectUrl, null);
    }

    public static PaymentInitResponse success(CouponId payment, String message, BigDecimal amount) {
        return new PaymentInitResponse(payment, PaymentInitStatus.SUCCESS, message, amount, null);
    }

    public static PaymentInitResponse redirect(CouponId payment, String message, BigDecimal amount, String redirectUrl) {
        return new PaymentInitResponse(payment, PaymentInitStatus.REDIRECT, message, amount, redirectUrl);
    }

    public static PaymentInitResponse processing(CouponId payment, String message, BigDecimal amount) {
        return new PaymentInitResponse(payment, PaymentInitStatus.PROCESSING, message, amount, null);
    }

    public static PaymentInitResponse failed(CouponId payment, String message) {
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

    public CouponId getPaymentId() {
        return paymentId;
    }

    public PaymentInitStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public Map<String, String> getAdditionalData() {
        return additionalData;
    }

    public enum PaymentInitStatus {
        SUCCESS,
        REDIRECT,
        PROCESSING,
        FAILED
    }
}
