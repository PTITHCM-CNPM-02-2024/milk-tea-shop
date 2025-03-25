package com.mts.backend.domain.promotion;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.value_object.DiscountName;
import com.mts.backend.domain.promotion.value_object.PromotionDiscountValue;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DomainException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Discount extends AbstractAggregateRoot<DiscountId> {
    @NonNull 
    private DiscountName name;
    @Nullable
    private String description;
    @NonNull
    private CouponId couponId;
    @NonNull
    private PromotionDiscountValue value;
    @NonNull
    private Money minRequiredOrderValue;
    @Nullable
    private Integer minRequiredProduct;
    @Nullable
    private LocalDateTime validFrom;
    @NonNull
    private LocalDateTime validUntil;
    @Nullable
    private Long maxUsage;
    @Nullable
    private Integer maxUsagePerCustomer;
    @Nullable
    private Long currentUsage;
    private boolean isActive;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    
    public Discount(@NonNull DiscountId id, @NonNull DiscountName name, @Nullable String description,
                    @NonNull CouponId couponId, @NonNull PromotionDiscountValue value,
                    @NonNull Money minRequiredOrderValue, @Nullable Integer minRequiredProduct, @Nullable LocalDateTime validFrom, @NonNull LocalDateTime validUntil, @Nullable Long maxUsage, @Nullable Integer maxUsagePerCustomer, @Nullable Long currentUsage, boolean isActive, LocalDateTime updatedAt) {
        super(id);
        this.name = Objects.requireNonNull(name, "Discount name must not be null");
        this.description = description;
        this.couponId = couponId;
        this.value = Objects.requireNonNull(value, "Discount value must not be null");
        this.minRequiredOrderValue = Objects.requireNonNull(minRequiredOrderValue, "Min required order value must not be null");
        this.minRequiredProduct = minRequiredProduct;
        this.validFrom = validFrom;
        this.validUntil = Objects.requireNonNull(validUntil, "Valid until must not be null");
        this.maxUsage = maxUsage;
        this.maxUsagePerCustomer = maxUsagePerCustomer;
        this.currentUsage = currentUsage;
        this.isActive = isActive;
        this.updatedAt = updatedAt;
        
        valid(validFrom, validUntil, maxUsage, maxUsagePerCustomer, currentUsage, minRequiredOrderValue, minRequiredProduct);
    }
    
    private void valid(LocalDateTime validFrom, LocalDateTime validUntil, Long maxUsage, Integer maxUsagePerCustomer,
                       Long currentUsage, Money minRequiredOrderValue, Integer minRequiredProduct){
        List<String> errors = new ArrayList<>();
        
        if (validFrom != null && validUntil != null && validFrom.isAfter(validUntil)){
            errors.add("Thời điểm bắt đầu" + validFrom + " phải trước thời điểm kết thúc" + validUntil);
        }
        
        if (maxUsage != null && maxUsagePerCustomer != null && maxUsage < maxUsagePerCustomer){
            errors.add("Số lần sử dụng tối đa cho mỗi khách hàng" + maxUsagePerCustomer + " phải nhỏ hơn số lần sử dụng tối đa" + maxUsage);
        }
        
        if (currentUsage != null && maxUsage != null && currentUsage > maxUsage){
            errors.add("Số lần đã sử dụng" + currentUsage + " phải nhỏ hơn số lần sử dụng tối đa" + maxUsage);
        }
        
        
        if (minRequiredOrderValue != null && minRequiredOrderValue.getValue() < 0){
            errors.add("Giá trị đơn hàng tối thiểu cần mua" + minRequiredOrderValue + " phải lớn hơn 0");
        }
        
        if (minRequiredProduct != null && minRequiredProduct < 1){
            errors.add("Số lượng sản phẩm tối thiểu cần mua" + minRequiredProduct + " phải lớn hơn 0");
        }
        
        if (!errors.isEmpty()){
            throw new DomainBusinessLogicException(errors);
        }
        
        
        
    }
    
    public boolean changeName(@NonNull DiscountName name){
        if (name.equals(this.name)){
            return false;
        }
        
        this.name = name;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeDescription(@Nullable String description){
        if (Objects.equals(this.description, description)){
            return false;
        }
        
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public void changeActive(boolean isActive){
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean changeCouponId(@NonNull CouponId couponId){
        if (Objects.equals(this.couponId, couponId)){
            return false;
        }
        
        this.couponId = couponId;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeDiscountValue(@NonNull PromotionDiscountValue value){
        if (this.value.equals(value)){
            return false;
        }
        
        this.value = value;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    
    
    public boolean changeMinRequiredOrderValue(@NonNull Money minRequiredOrderValue){
        if (Objects.equals(this.minRequiredOrderValue, minRequiredOrderValue)){
            return false;
        }
        
        this.minRequiredOrderValue = minRequiredOrderValue;
        this.updatedAt = LocalDateTime.now();
        valid(validFrom, validUntil, maxUsage, maxUsagePerCustomer, currentUsage, minRequiredOrderValue, minRequiredProduct);
        return true;
    }
    
    public boolean changeMinRequiredProduct(@Nullable Integer minRequiredProduct){
        if (Objects.equals(this.minRequiredProduct, minRequiredProduct)){
            return false;
        }
        
        this.minRequiredProduct = minRequiredProduct;
        this.updatedAt = LocalDateTime.now();
        valid(validFrom, validUntil, maxUsage, maxUsagePerCustomer, currentUsage, minRequiredOrderValue, minRequiredProduct);
        return true;
    }
    
    public boolean changeValidFrom(@Nullable LocalDateTime validFrom){
        if (Objects.equals(this.validFrom, validFrom)){
            return false;
        }
        
        this.validFrom = validFrom;
        this.updatedAt = LocalDateTime.now();
        valid(validFrom, validUntil, maxUsage, maxUsagePerCustomer, currentUsage, minRequiredOrderValue, minRequiredProduct);
        return true;
    }
    
    
    public boolean changeValidUntil(@NonNull LocalDateTime validUntil){
        if (Objects.equals(this.validUntil, validUntil)){
            return false;
        }
        
        this.validUntil = validUntil;
        this.updatedAt = LocalDateTime.now();
        valid(validFrom, validUntil, maxUsage, maxUsagePerCustomer, currentUsage, minRequiredOrderValue, minRequiredProduct);
        return true;
    }
    
    public boolean changeMaxUsage(@Nullable Long maxUsage){
        if (Objects.equals(this.maxUsage, maxUsage)){
            return false;
        }
        
        this.maxUsage = maxUsage;
        this.updatedAt = LocalDateTime.now();
        valid(validFrom, validUntil, maxUsage, maxUsagePerCustomer, currentUsage, minRequiredOrderValue, minRequiredProduct);
        return true;
    }
    
    public boolean changeMaxUsagePerCustomer(@Nullable Integer maxUsagePerCustomer){
        if (Objects.equals(this.maxUsagePerCustomer, maxUsagePerCustomer)){
            return false;
        }
        
        this.maxUsagePerCustomer = maxUsagePerCustomer;
        this.updatedAt = LocalDateTime.now();
        valid(validFrom, validUntil, maxUsage, maxUsagePerCustomer, currentUsage, minRequiredOrderValue,
                minRequiredProduct);
        return true;
    }
    
    public boolean changeCurrentUsage(@Nullable Long currentUsage){
        if (Objects.equals(this.currentUsage, currentUsage)){
            return false;
        }
        
        this.currentUsage = currentUsage;
        this.updatedAt = LocalDateTime.now();
        valid(validFrom, validUntil, maxUsage, maxUsagePerCustomer, currentUsage, minRequiredOrderValue, minRequiredProduct);
        return true;
    }
    
    public Optional<Long> increaseCurrentUsage(){
        if (maxUsage != null && currentUsage != null && currentUsage >= maxUsage){
            throw new DomainException("Số lần sử dụng tối đa đã được đạt");
        }
        
        currentUsage = currentUsage == null ? 1 : currentUsage + 1;
        this.updatedAt = LocalDateTime.now();
        return Optional.of(currentUsage);
    }
    
    public Optional<Long> decreaseCurrentUsage(){
        if (currentUsage == null || currentUsage <= 0){
            throw new DomainException("Số lần sử dụng không thể âm");
        }
        
        currentUsage = currentUsage - 1;
        this.updatedAt = LocalDateTime.now();
        return Optional.of(currentUsage);
    }
    
    public DiscountName getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public CouponId getCouponId() {
        return couponId;
    }
    
    public PromotionDiscountValue getDiscountValue() {
        return value;
    }
    
    public Money getMinRequiredOrderValue() {
        return minRequiredOrderValue;
    }
    
    public Optional<Integer> getMinRequiredProduct() {
        return Optional.ofNullable(minRequiredProduct);
    }
    
    public Optional<LocalDateTime> getValidFrom() {
        return Optional.ofNullable(validFrom);
    }
    
    public LocalDateTime getValidUntil() {
        return validUntil;
    }
    
    public Optional<Long> getMaxUsage() {
        return Optional.ofNullable(maxUsage);
    }
    
    public Optional<Integer> getMaxUsagePerCustomer() {
        return Optional.ofNullable(maxUsagePerCustomer);
    }
    
    public Optional<Long> getCurrentUsage() {
        return Optional.ofNullable(currentUsage);
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    
    
    
}
