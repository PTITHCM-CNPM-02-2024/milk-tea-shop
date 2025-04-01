package com.mts.backend.application.billing.handler;

import com.mts.backend.application.billing.command.CreateInvoiceCommand;
import com.mts.backend.application.order.response.OrderDiscountDetailResponse;
import com.mts.backend.application.order.response.OrderProductDetailResponse;
import com.mts.backend.application.order.response.OrderTableDetailResponse;
import com.mts.backend.application.payment.response.PaymentDetailResponse;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.OrderDiscountEntity;
import com.mts.backend.domain.order.OrderEntity;
import com.mts.backend.domain.payment.PaymentEntity;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CreateInvoiceCommandHandler implements ICommandHandler<CreateInvoiceCommand, CommandResult> {


    private static final String TEMPLATE_PATH = "templates/invoice_template.html";
    private static final String COMPANY_NAME = "Công ty TNHH MTS";
    private static final String COMPANY_ADDRESS = "Số 1, Man Thiện, P. Hiệp Phú, Q.9, TP.HCM";
    private static final String COMPANY_PHONE = "0901234567";
    private static final String COMPANY_EMAIL = "mts@gmail.com";

    public CreateInvoiceCommandHandler(){
    }

    @Override
    public CommandResult handle(CreateInvoiceCommand command) {
        Objects.requireNonNull(command, "CreateInvoiceCommand is required");

        try {
            String template = loadTemplate(TEMPLATE_PATH);
            template = fillCompanyInfo(template);
            template = fillCustomerInfo(template, command.getOrder());
            template = fillEmployeeInfo(template, command.getOrder());
            template = fillOrderInfo(template, command.getOrder());
            template = fillTableInfo(template, command.getOrder());
            template = fillDiscountInfo(template, command.getOrder());
            template = fillProductItems(template, command.getOrder());
            template = fillPaymentInfo(template, command.getOrder(), command.getPayment());

            return CommandResult.success(template);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating invoice: " + e.getMessage(), e);
        }
    }

    private String loadTemplate(String path) throws Exception {
        try (var reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource(path).getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private String fillCompanyInfo(String template) {
        template = template.replace("{{companyName}}", COMPANY_NAME);
        template = template.replace("{{companyAddress}}", COMPANY_ADDRESS);
        template = template.replace("{{companyPhone}}", COMPANY_PHONE);
        template = template.replace("{{companyEmail}}", COMPANY_EMAIL);
        return template;
    }

    private String fillOrderInfo(String template, OrderEntity order) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        template = template.replace("{{invoiceNumber}}", "DH-%s".formatted(order.getId()));
        template = template.replace("{{invoiceDate}}", formatter.format(order.getOrderTime()));
        template = template.replace("{{notes}}", order.getCustomizeNote().orElse(""));
        template = template.replace("{{currentDate}}", LocalDateTime.now().format(formatter));

        return template;
    }

    private String fillTableInfo(String template, OrderEntity order) {
        var tableList = getTables(order);

        if (tableList.isEmpty()) {
            template = template.replace("{{tableName}}", "Không có bàn");
            template = template.replace("{{serviceType}}", "Mang đi");
        } else {
            String tables = tableList.stream()
                    .map(OrderTableDetailResponse::getTableNumber)
                    .collect(Collectors.joining(", "));

            template = template.replace("{{tableName}}", tables);
            template = template.replace("{{serviceType}}", "Tại chỗ");
        }

        return template;
    }

    private String fillDiscountInfo(String template, OrderEntity order) {
        var discounts = getDiscounts(order);

        if (discounts.isEmpty()) {
            template = template.replaceAll("\\{\\{#if hasCouponPromotion\\}\\}([\\s\\S]*?)\\{\\{/if\\}\\}", "");
            return template;
        }

        String discountNames = discounts.stream()
                .map(OrderDiscountDetailResponse::getName)
                .collect(Collectors.joining(", "));

        String discountValues = discounts.stream()
                .map(OrderDiscountDetailResponse::getDiscountValue)
                .collect(Collectors.joining(", "));

        String couponCodes = discounts.stream()
                .map(OrderDiscountDetailResponse::getCouponCode)
                .collect(Collectors.joining(", "));
        

        template = template.replace("{{#if hasCouponPromotion}}", "");
        template = template.replace("{{/if}}", "");
        template = template.replace("{{couponCode}}", couponCodes);
        return template;
    }

    private String fillProductItems(String template, OrderEntity order) {
        DecimalFormat currency = new DecimalFormat("#,###");
        var products = getProducts(order);

        StringBuilder items = new StringBuilder();
        int itemNo = 1;

        for (var item : products) {
            items.append("<tr>")
                    .append("<td>").append(itemNo++).append("</td>")
                    .append("<td>").append(item.getProductName()).append("</td>")
                    .append("<td>").append("N/A".equals(item.getSize()) ? "-" : item.getSize()).append("</td>")
                    .append("<td>").append(item.getQuantity()).append("</td>")
                    .append("<td class=\"text-right\">").append(currency.format(item.getPrice())).append("</td>")
                    .append("<td class=\"text-right\">").append(currency.format(item.getTotalPrice())).append("</td>")
                    .append("</tr>");

            if (item.getProductOption() != null && !item.getProductOption().isEmpty()) {
                items.append("<tr class=\"note-row\">")
                        .append("<td></td>")
                        .append("<td colspan=\"5\">Ghi chú: ").append(item.getProductOption()).append("</td>")
                        .append("</tr>");
            }
        }

        template = template.replace("{{invoiceItems}}", items.toString());

        // Total amount
        template = template.replace("{{totalAmount}}",
                currency.format(order.getTotalAmount().orElse(Money.ZERO).getValue()));
        
        // Final amount
        template = template.replace("{{grandTotal}}",
                currency.format(order.getFinalAmount().orElse(Money.ZERO).getValue()));
        
        // Total discount
        Money totalDiscount = order.getOrderDiscounts().stream()
                .map(OrderDiscountEntity::getDiscountAmount)
                .reduce(Money.ZERO, Money::add);

        template = template.replace("{{discountAmount}}", currency.format(totalDiscount.getValue()));

        return template;
    }

    private String fillPaymentInfo(String template, OrderEntity order, PaymentEntity payment) {
        DecimalFormat currency = new DecimalFormat("#,###");
        var paymentDetail = getPayment(payment);

        template = template.replace("{{paymentMethod}}", paymentDetail.getPaymentMethod());
        template = template.replace("{{amountPaid}}", currency.format(paymentDetail.getAmountPaid()));
        template = template.replace("{{changeAmount}}", currency.format(paymentDetail.getChange()));

        return template;
    }

    private List<OrderTableDetailResponse> getTables(OrderEntity order) {
        return order.getOrderTables().stream()
                .map(orderTable -> {
                    var table = orderTable.getTable();
                    return OrderTableDetailResponse.builder()
                            .tableNumber(table.getTableNumber().getValue())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<OrderProductDetailResponse> getProducts(OrderEntity order) {
        return order.getOrderProducts().stream()
                .map(orderProduct -> {
                    var productPrice = orderProduct.getProductPriceEntity();

                    var product = productPrice.getProductEntity();

                    var size = productPrice.getSize();
                    return OrderProductDetailResponse.builder()
                            .productName(product.getName().getValue())
                            .size(size.getName().getValue())
                            .price(productPrice.getPrice().getValue())
                            .quantity(orderProduct.getQuantity())
                            .productOption(orderProduct.getOption())
                            .totalPrice(productPrice.getPrice().getValue()
                                    .multiply(BigDecimal.valueOf(orderProduct.getQuantity())))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<OrderDiscountDetailResponse> getDiscounts(OrderEntity order) {
        return order.getOrderDiscounts().stream()
                .map(orderDiscount -> {
                    var discount = orderDiscount.getDiscount();

                    var coupon = discount.getCouponEntity();

                    return OrderDiscountDetailResponse.builder()
                            .name(discount.getName().getValue())
                            .couponCode(coupon.getCoupon().getValue())
                            .discountValue(discount.getPromotionDiscountValue().getDescription())
                            .discountAmount(orderDiscount.getDiscountAmount().getValue())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private PaymentDetailResponse getPayment(PaymentEntity payment){
        var paymentMethod = payment.getPaymentMethod();

        return PaymentDetailResponse.builder()
                .paymentMethod(paymentMethod.getPaymentName().getValue())
                .amountPaid(payment.getAmountPaid()
                        .map(Money::getValue)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy số tiền đã thanh toán")))
                .change(payment.getChangeAmount()
                        .map(Money::getValue)
                        .orElse(BigDecimal.ZERO))
                .build();
    }

    private String fillCustomerInfo(String template, OrderEntity order) {
        if (order.getCustomerEntity().isEmpty()) {
            template = template.replace("{{clientName}}", "Khách vãng lai");
            template = template.replace("{{clientAddress}}", "");
            template = template.replace("{{clientEmail}}", "");
            template = template.replace("{{clientPhone}}", "");
            template = template.replace("{{memberPromotionDescription}}", "");

            template = template.replaceAll("\\{\\{#if hasMemberPromotion\\}\\}([\\s\\S]*?)\\{\\{/if\\}\\}", "");
            return template;
        }

        var customer = order.getCustomerEntity().get();

        var membershipType = customer.getMembershipTypeEntity();

        template = template.replace("{{clientName}}", customer.getFullName().orElse(""));
        template = template.replace("{{clientAddress}}", "");
        template = template.replace("{{clientEmail}}", customer.getEmail().map(Object::toString).orElse(""));
        template = template.replace("{{clientPhone}}", customer.getPhone().getValue());
        template = template.replace("{{memberPromotionDescription}}",
                membershipType.getType().getValue() + " - " + membershipType.getMemberDiscountValue().getDescription());
    // Hiển thị phần hasMemberPromotion nếu có thông tin thành viên
    template = template.replace("{{#if hasMemberPromotion}}", "");
    template = template.replace("{{/if}}", "");

        return template;
    }

    private String fillEmployeeInfo(String template, OrderEntity order) {
        var employee = order.getEmployeeEntity();

        template = template.replace("{{staffName}}", employee.getFirstName().getValue() + " " + employee.getLastName().getValue());
        template = template.replace("{{staffId}}", String.valueOf(employee.getId()));

        return template;
    }
}