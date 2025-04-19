package com.mts.backend.domain.store;

import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.store.identifier.StoreId;
import com.mts.backend.domain.store.value_object.Address;
import com.mts.backend.domain.store.value_object.StoreName;
import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "store", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Store extends BaseEntity<Integer> {
    @Id
    @Comment("Mã cửa hàng")
    @Column(name = "store_id", columnDefinition = "tinyint UNSIGNED")
    @NotNull
    @Getter
    private Integer id;

    public Store(@NotNull Integer id, @NotNull @Size(max = 100, message = "Tên cửa hàng không được vượt quá 100 ký tự") String name, @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự") @NotBlank(message = "Địa chỉ không được để trống") String address, @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự") @NotBlank(message = "Số điện thoại không được để trống") @Pattern(regexp = "(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)", message = "Số điện thoại không hợp lệ") String phone, @NotNull @Size(max = 100, message = "Email không được vượt quá 100 ký tự") @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Email không hợp lệ") String email, @NotNull LocalDate openingDate, @NotNull @Size(max = 20, message = "Mã số thuế không được vượt quá 20 ký tự") String taxCode, @NotNull LocalTime openingTime, @NotNull LocalTime closingTime) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.openingDate = openingDate;
        this.taxCode = taxCode;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        
        validate(openingTime, closingTime);
    }

    public Store() {
    }
    
    private void validate(@NotNull LocalTime openingTime,@NotNull LocalTime closingTime) {
        if (closingTime.isBefore(openingTime)) {
            throw new DomainException("Thời gian đóng cửa không được nhỏ hơn thời gian mở cửa");
        }
    }

    public static StoreBuilder builder() {
        return new StoreBuilder();
    }

    public boolean setId(@NotNull StoreId storeId) {
        if (StoreId.of(this.id).equals(storeId)) {
            return false;
        }

        this.id = storeId.getValue();
        return true;
    }

    @Comment("Tên cửa hàng")
    @Column(name = "name", nullable = false, length = 100)
    @NotNull
    @Size(max = 100, message = "Tên cửa hàng không được vượt quá 100 ký tự")
    private String name;

    public boolean setName(@NotNull StoreName storeName) {
        if (StoreName.of(this.name).equals(storeName)) {
            return false;
        }

        this.name = storeName.getValue();
        return true;
    }

    public StoreName getStoreName() {
        return StoreName.of(this.name);
    }

    @Comment("Địa chỉ")
    @Column(name = "address", nullable = false)
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    public boolean setAddress(@NotNull Address address) {
        if (Address.of(this.address).equals(address)) {
            return false;
        }

        this.address = address.getValue();
        return true;
    }

    public Address getAddress() {
        return Address.of(this.address);
    }

    @Comment("Số điện thoại")
    @Column(name = "phone", nullable = false, length = 15)
    @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
    @NotBlank(message = "Số điện thoại không được để trống")
    @jakarta.validation.constraints.Pattern(regexp = "(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)", message = "Số điện thoại không hợp lệ")
    private String phone;

    public boolean setPhone(@NotNull PhoneNumber phoneNumber) {
        if (PhoneNumber.of(this.phone).equals(phoneNumber)) {
            return false;
        }

        this.phone = phoneNumber.getValue();
        return true;
    }

    public PhoneNumber getPhone() {
        return PhoneNumber.of(this.phone);
    }

    @Comment("Email")
    @Column(name = "email", length = 100, nullable = false)
    @NotNull
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Email không hợp lệ")
    private String email;

    public boolean setEmail(@NotNull Email email) {
        if (Email.of(this.email).equals(email)) {
            return false;
        }

        this.email = email.getValue();
        return true;
    }

    public Email getEmail() {
        return Email.of(this.email);
    }

    @Comment("Ngày khai trương")
    @Column(name = "opening_date", nullable = false)
    @NotNull
    private LocalDate openingDate;

    @Comment("Mã số thuế")
    @Column(name = "tax_code", length = 20)
    @NotNull
    @Size(max = 20, message = "Mã số thuế không được vượt quá 20 ký tự")
    private String taxCode;

    @Comment("Thời gian mở cửa")
    @Column(name = "opening_time", nullable = false)
    @NotNull
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    @NotNull
    private LocalTime closingTime;


    public boolean setTaxCode(@NotNull String taxCode) {
        if (this.taxCode.equals(taxCode)) {
            return false;
        }

        this.taxCode = taxCode;
        return true;
    }

    public boolean setOpeningTime(@NotNull LocalTime openingTime) {
        if (this.openingTime.equals(openingTime)) {
            return false;
        }

        this.openingTime = openingTime;
        validate(openingTime, this.closingTime);
        return true;
    }

    public boolean setClosingTime(LocalTime closingTime) {
        if (this.closingTime.equals(closingTime)) {
            return false;
        }

        if (closingTime.isBefore(this.openingTime)) {
            throw new DomainException("Thời gian đóng cửa không được nhỏ hơn thời gian mở cửa");
        }

        this.closingTime = closingTime;
        validate(this.openingTime, closingTime);
        return true;
    }

    public boolean setOpeningDate(LocalDate openingDate) {
        if (this.openingDate.equals(openingDate)) {
            return false;
        }

        this.openingDate = openingDate;
        return true;
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Store that = (Store) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public static class StoreBuilder {
        private @NotNull Integer id;
        private @NotNull
        @Size(max = 100, message = "Tên cửa hàng không được vượt quá 100 ký tự") String name;
        private @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
        @NotBlank(message = "Địa chỉ không được để trống") String address;
        private @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)", message = "Số điện thoại không hợp lệ") String phone;
        private @NotNull
        @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
        @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Email không hợp lệ") String email;
        private @NotNull LocalDate openingDate;
        private @NotNull
        @Size(max = 20, message = "Mã số thuế không được vượt quá 20 ký tự") String taxCode;
        private @NotNull LocalTime openingTime;
        private @NotNull LocalTime closingTime;

        StoreBuilder() {
        }

        public StoreBuilder id(@NotNull Integer id) {
            this.id = id;
            return this;
        }

        public StoreBuilder name(@NotNull StoreName storeName) {
            this.name = storeName.getValue();
            return this;
        }

        public StoreBuilder address(@NotNull Address address) {
            this.address = address.getValue();
            return this;
        }

        public StoreBuilder phone(@NotNull PhoneNumber phoneNumber) {
            this.phone = phoneNumber.getValue();
            return this;
        }

        public StoreBuilder email(@NotNull Email email) {
            this.email = email.getValue();
            return this;
        }

        public StoreBuilder openingDate(@NotNull LocalDate openingDate) {
            this.openingDate = openingDate;
            return this;
        }

        public StoreBuilder taxCode(@NotNull @Size(max = 20, message = "Mã số thuế không được vượt quá 20 ký tự") String taxCode) {
            this.taxCode = taxCode;
            return this;
        }

        public StoreBuilder openingTime(@NotNull LocalTime openingTime) {
            this.openingTime = openingTime;
            return this;
        }

        public StoreBuilder closingTime(@NotNull LocalTime closingTime) {
            this.closingTime = closingTime;
            return this;
        }

        public Store build() {
            return new Store(this.id, this.name, this.address, this.phone, this.email, this.openingDate, this.taxCode, this.openingTime, this.closingTime);
        }

        public String toString() {
            return "Store.StoreBuilder(id=" + this.id + ", name=" + this.name + ", address=" + this.address + ", phone=" + this.phone + ", email=" + this.email + ", openingDate=" + this.openingDate + ", taxCode=" + this.taxCode + ", openingTime=" + this.openingTime + ", closingTime=" + this.closingTime + ")";
        }
    }
}