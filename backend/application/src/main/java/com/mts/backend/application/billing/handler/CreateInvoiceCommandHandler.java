package com.mts.backend.application.billing.handler;

import com.mts.backend.application.billing.command.CreateInvoiceCommand;
import com.mts.backend.application.order.response.OrderDiscountDetailResponse;
import com.mts.backend.application.order.response.OrderProductDetailResponse;
import com.mts.backend.application.order.response.OrderTableDetailResponse;
import com.mts.backend.application.payment.response.PaymentDetailResponse;
import com.mts.backend.application.payment.response.PaymentMethodDetailResponse;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.OrderDiscount;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.domain.staff.Employee;
import com.mts.backend.domain.store.jpa.JpaStoreRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
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

    private final JpaStoreRepository storeRepository;

    public CreateInvoiceCommandHandler(JpaStoreRepository storeRepository) {
        this.storeRepository = storeRepository;
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

        var store = storeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin cửa hàng"));

        final String COMPANY_NAME = store.getName().getValue();
        final String COMPANY_ADDRESS = store.getAddress().getValue();
        final String COMPANY_PHONE = store.getPhone().getValue();
        final String COMPANY_EMAIL = store.getEmail().getValue();

        template = template.replace("{{companyName}}", COMPANY_NAME);
        template = template.replace("{{companyAddress}}", COMPANY_ADDRESS);
        template = template.replace("{{companyPhone}}", COMPANY_PHONE);
        template = template.replace("{{companyEmail}}", COMPANY_EMAIL);
        return template;
    }

    private String fillOrderInfo(String template, Order order) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        template = template.replace("{{invoiceNumber}}", "DH-%s".formatted(order.getId()));
        template = template.replace("{{invoiceDate}}", formatter.format(order.getOrderTime()));
        template = template.replace("{{notes}}", order.getCustomizeNote().orElse(""));
        template = template.replace("{{currentDate}}", LocalDateTime.now().format(formatter));
        template = template.replace("{{currentUser}}",
                "Nhân viên: %s".formatted(order.getEmployee().map(e -> e.getFirstName().getValue()).orElse("")));

        return template;
    }

    private String fillTableInfo(String template, Order order) {
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

    private String fillDiscountInfo(String template, Order order) {
        var discounts = getDiscounts(order);
        if (discounts.isEmpty()) {
            // Regex to remove the entire coupon promotion block, including the surrounding div
            String regex = "(?s)<div\\s+class=\"promotion\">\\s*<p><strong>Mã giảm giá áp dụng:</strong>.*?</div>";
            template = template.replaceAll(regex, "");

            // Regex to remove the discount row in the totals table
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

    private String fillProductItems(String template, Order order) {
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
                .map(OrderDiscount::getDiscountAmount)
                .reduce(Money.ZERO, Money::add);

        template = template.replace("{{discountAmount}}", currency.format(totalDiscount.getValue()));

        return template;
    }

    private String fillPaymentInfo(String template, Order order, Payment payment) {
        DecimalFormat currency = new DecimalFormat("#,###");
        var paymentDetail = getPayment(payment);

        template = template.replace("{{paymentMethod}}", paymentDetail.getPaymentMethod()
                .getName());
        template = template.replace("{{amountPaid}}", currency.format(paymentDetail.getAmountPaid()));
        template = template.replace("{{changeAmount}}", currency.format(paymentDetail.getChange()));

        return template;
    }

    private List<OrderTableDetailResponse> getTables(Order order) {
        return order.getOrderTables().stream()
                .map(orderTable -> {
                    var table = orderTable.getTable();
                    return OrderTableDetailResponse.builder()
                            .tableNumber(table.getTableNumber().getValue())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<OrderProductDetailResponse> getProducts(Order order) {
        return order.getOrderProducts().stream()
                .map(orderProduct -> {
                    var productPrice = orderProduct.getPrice();

                    var product = productPrice.getProduct();

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

    private List<OrderDiscountDetailResponse> getDiscounts(Order order) {
        return order.getOrderDiscounts().stream()
                .map(orderDiscount -> {
                    var discount = orderDiscount.getDiscount();

                    var coupon = discount.getCoupon();

                    return OrderDiscountDetailResponse.builder()
                            .name(discount.getName().getValue())
                            .couponCode(coupon.getCoupon().getValue())
                            .discountValue(discount.getPromotionDiscountValue().getDescription())
                            .discountAmount(orderDiscount.getDiscountAmount().getValue())
                            .build();
                })
                .toList();
    }

    private PaymentDetailResponse getPayment(Payment payment) {
        var paymentMethod = payment.getPaymentMethod();

        return PaymentDetailResponse.builder()
                .paymentMethod(new PaymentMethodDetailResponse(
                        paymentMethod.getId(),
                        paymentMethod.getName().getValue(),
                        paymentMethod.getDescription().orElse(null)))
                .amountPaid(payment.getAmountPaid()
                        .map(Money::getValue)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy số tiền đã thanh toán")))
                .change(payment.getChangeAmount()
                        .map(Money::getValue)
                        .orElse(BigDecimal.ZERO))
                .build();
    }

    private String fillCustomerInfo(String template, Order order) {
        if (order.getCustomer().isEmpty()) {
            template = template.replace("{{clientName}}", "Khách vãng lai");
            template = template.replace("{{clientAddress}}", "");
            template = template.replace("{{clientEmail}}", "");
            template = template.replace("{{clientPhone}}", "");
            template = template.replace("{{memberPromotionDescription}}", "");

            template = template.replaceAll("\\{\\{#if hasMemberPromotion\\}\\}([\\s\\S]*?)\\{\\{/if\\}\\}", "");
            return template;
        }

        var customer = order.getCustomer().get();

        var membershipType = customer.getMembershipType();

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

    private String fillEmployeeInfo(String template, Order order) {
        var employee = order.getEmployee();

        template = template.replace("{{staffName}}",
                employee.map(e -> e.getFirstName().getValue() + " " + e.getLastName().getValue())
                        .orElse("Nhân viên không xác định"));
        template = template.replace("{{staffId}}", String.valueOf(
                employee.map(Employee::getId)
                        .orElse(-1L)));

        return template;
    }
}