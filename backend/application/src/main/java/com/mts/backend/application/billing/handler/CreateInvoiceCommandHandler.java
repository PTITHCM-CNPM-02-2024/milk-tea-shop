package com.mts.backend.application.billing.handler;

import com.mts.backend.application.billing.command.CreateInvoiceCommand;
import com.mts.backend.application.order.response.OrderDiscountDetailResponse;
import com.mts.backend.application.order.response.OrderProductDetailResponse;
import com.mts.backend.application.order.response.OrderTableDetailResponse;
import com.mts.backend.application.payment.response.PaymentDetailResponse;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.repository.ICustomerRepository;
import com.mts.backend.domain.customer.repository.IMembershipTypeRepository;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.entity.OrderDiscount;
import com.mts.backend.domain.payment.Payment;
import com.mts.backend.domain.payment.repository.IPaymentMethodRepository;
import com.mts.backend.domain.product.repository.IProductRepository;
import com.mts.backend.domain.product.repository.ISizeRepository;
import com.mts.backend.domain.promotion.repository.ICouponRepository;
import com.mts.backend.domain.promotion.repository.IDiscountRepository;
import com.mts.backend.domain.promotion.value_object.PromotionDiscountValue;
import com.mts.backend.domain.staff.repository.IEmployeeRepository;
import com.mts.backend.domain.store.repository.IServiceTableRepository;
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
    private final IProductRepository productRepository;
    private final IServiceTableRepository serviceTableRepository;
    private final IDiscountRepository discountRepository;
    private final IEmployeeRepository employeeRepository;
    private final ICustomerRepository customerRepository;
    private final ISizeRepository sizeRepository;
    private final ICouponRepository couponRepository;
    private final IPaymentMethodRepository paymentMethodRepository;
    private final IMembershipTypeRepository membershipTypeRepository;

    private static final String TEMPLATE_PATH = "templates/invoice_template.html";
    private static final String COMPANY_NAME = "Công ty TNHH MTS";
    private static final String COMPANY_ADDRESS = "Số 1, Man Thiện, P. Hiệp Phú, Q.9, TP.HCM";
    private static final String COMPANY_PHONE = "0901234567";
    private static final String COMPANY_EMAIL = "mts@gmail.com";

    public CreateInvoiceCommandHandler(
            IProductRepository productRepository,
            IServiceTableRepository serviceTableRepository,
            IDiscountRepository discountRepository,
            IEmployeeRepository employeeRepository,
            ICustomerRepository customerRepository,
            ISizeRepository sizeRepository,
            ICouponRepository couponRepository,
            IPaymentMethodRepository paymentMethodRepository,
            IMembershipTypeRepository membershipTypeRepository) {
        this.productRepository = productRepository;
        this.serviceTableRepository = serviceTableRepository;
        this.discountRepository = discountRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.sizeRepository = sizeRepository;
        this.couponRepository = couponRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.membershipTypeRepository = membershipTypeRepository;
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

    private String fillOrderInfo(String template, Order order) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        template = template.replace("{{invoiceNumber}}", "DH-%s".formatted(order.getId()));
        template = template.replace("{{invoiceDate}}", formatter.format(order.getOrderTime()));
        template = template.replace("{{notes}}", order.getCustomizeNote().orElse(""));
        template = template.replace("{{currentDate}}", LocalDateTime.now().format(formatter));

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
            template = template.replaceAll("\\{\\{#if hasPromotion\\}\\}([\\s\\S]*?)\\{\\{/if\\}\\}", "");
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
        

        template = template.replace("{{#if hasPromotion}}", "");
        template = template.replace("{{/if}}", "");
        template = template.replace("{{promotionCode}}", couponCodes);
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
                currency.format(order.getTotalAmount().orElse(Money.ZERO).getAmount()));
        
        // Final amount
        template = template.replace("{{grandTotal}}",
                currency.format(order.getFinalAmount().orElse(Money.ZERO).getAmount()));
        
        // Total discount
        Money totalDiscount = order.getOrderDiscounts().stream()
                .map(OrderDiscount::getDiscountAmount)
                .reduce(Money.ZERO, Money::add);

        template = template.replace("{{discountAmount}}", currency.format(totalDiscount.getAmount()));

        return template;
    }

    private String fillPaymentInfo(String template, Order order, Payment payment) {
        DecimalFormat currency = new DecimalFormat("#,###");
        var paymentDetail = getPayment(payment);

        template = template.replace("{{paymentMethod}}", paymentDetail.getPaymentMethod());
        template = template.replace("{{amountPaid}}", currency.format(paymentDetail.getAmountPaid()));
        template = template.replace("{{changeAmount}}", currency.format(paymentDetail.getChange()));

        return template;
    }

    private List<OrderTableDetailResponse> getTables(Order order) {
        return order.getOrderTables().stream()
                .map(orderTable -> {
                    var table = serviceTableRepository.findById(orderTable.getTableId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Không tìm thấy bàn với id: " + orderTable.getTableId().getValue()));
                    return OrderTableDetailResponse.builder()
                            .tableNumber(table.getTableNumber().getValue())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<OrderProductDetailResponse> getProducts(Order order) {
        return order.getOrderProducts().stream()
                .map(orderProduct -> {
                    var product = productRepository.findByPriceId(orderProduct.getProductPriceId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Không tìm thấy sản phẩm với id: " +
                                            orderProduct.getProductPriceId().getValue()));

                    var productPrice = product.getPrice(orderProduct.getProductPriceId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Không tìm thấy giá sản phẩm với id: " +
                                            orderProduct.getProductPriceId().getValue()));

                    var size = sizeRepository.findById(productPrice.getSizeId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Không tìm thấy size với id: " + productPrice.getSizeId()));

                    return OrderProductDetailResponse.builder()
                            .productName(product.getName().getValue())
                            .size(size.getName().getValue())
                            .price(productPrice.getPrice().getAmount())
                            .quantity(orderProduct.getQuantity())
                            .productOption(orderProduct.getOption())
                            .totalPrice(productPrice.getPrice().getAmount()
                                    .multiply(BigDecimal.valueOf(orderProduct.getQuantity())))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<OrderDiscountDetailResponse> getDiscounts(Order order) {
        return order.getOrderDiscounts().stream()
                .map(orderDiscount -> {
                    var discount = discountRepository.findById(orderDiscount.getDiscountId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Không tìm thấy khuyến mãi với id: " +
                                            orderDiscount.getDiscountId().getValue()));

                    var coupon = couponRepository.findById(discount.getCouponId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Không tìm thấy mã giảm giá với id: " +
                                            discount.getCouponId().getValue()));

                    return OrderDiscountDetailResponse.builder()
                            .name(discount.getName().getValue())
                            .couponCode(coupon.getCoupon().getValue())
                            .discountValue(orderDiscount.getDiscountValue()
                                    .map(PromotionDiscountValue::getDescription)
                                    .orElse(""))
                            .discountAmount(orderDiscount.getDiscountAmount().getAmount())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private PaymentDetailResponse getPayment(Payment payment){
        var paymentMethod = paymentMethodRepository.findById(payment.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy phương thức thanh toán với id: " +
                                payment.getPaymentMethodId().getValue()));

        return PaymentDetailResponse.builder()
                .paymentMethod(paymentMethod.getName().getValue())
                .amountPaid(payment.getAmountPaid()
                        .map(Money::getAmount)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy số tiền đã thanh toán")))
                .change(payment.getChangeAmount()
                        .map(Money::getAmount)
                        .orElse(BigDecimal.ZERO))
                .build();
    }

    private String fillCustomerInfo(String template, Order order) {
        if (order.getCustomerId().isEmpty()) {
            template = template.replace("{{clientName}}", "Khách vãng lai");
            template = template.replace("{{clientAddress}}", "");
            template = template.replace("{{clientEmail}}", "");
            template = template.replace("{{clientPhone}}", "");
            template = template.replace("{{promotionDescription}}", "");
            return template;
        }

        var customer = customerRepository.findById(order.getCustomerId().get())
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy khách hàng với id: " +
                                order.getCustomerId().get().getValue()));
        var membershipType = membershipTypeRepository.findById(customer.getMembershipTypeId())
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy loại thành viên với id: " +
                                customer.getMembershipTypeId().getValue()));

        template = template.replace("{{clientName}}", customer.getFullName());
        template = template.replace("{{clientAddress}}", "");
        template = template.replace("{{clientEmail}}", customer.getEmail().map(Object::toString).orElse(""));
        template = template.replace("{{clientPhone}}", customer.getPhoneNumber().getValue());
        template = template.replace("{{promotionDescription}}",
                membershipType.getName().getValue() + " - " + membershipType.getDiscountValue());

        return template;
    }

    private String fillEmployeeInfo(String template, Order order) {
        var employee = employeeRepository.findById(order.getEmployeeId())
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy nhân viên với id: " +
                                order.getEmployeeId().getValue()));

        template = template.replace("{{staffName}}", employee.getFirstName().getValue() + " " + employee.getLastName().getValue());
        template = template.replace("{{staffId}}", employee.getId().toString());

        return template;
    }
}