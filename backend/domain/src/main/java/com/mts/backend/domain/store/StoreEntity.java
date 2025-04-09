package com.mts.backend.domain.store;

import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.store.identifier.StoreId;
import com.mts.backend.domain.store.value_object.Address;
import com.mts.backend.domain.store.value_object.StoreName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@Entity
@Table(name = "store", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreEntity extends BaseEntity <Integer> {
    @Id
    @Comment("Mã cửa hàng")
    @Column(name = "store_id", columnDefinition = "tinyint UNSIGNED")
    @NotNull
    private Integer id;

    @Comment("Tên cửa hàng")
    @Column(name = "name", nullable = false, length = 100)
    @NotNull
    @Convert(converter = StoreName.StoreNameConverter.class)
    private StoreName name;

    @Comment("Địa chỉ")
    @Column(name = "address", nullable = false)
    @Convert(converter = Address.AddressConverter.class)
    @NotNull
    private Address address;

    @Comment("Số điện thoại")
    @Column(name = "phone", nullable = false, length = 15)
    @NotNull
    @Convert(converter = PhoneNumber.PhoneNumberConverter.class)
    private PhoneNumber phone;

    @Comment("Email")
    @Column(name = "email", length = 100, nullable = false)
    @NotNull
    @Convert(converter = Email.EmailConverter.class)
    private Email email;

    @Comment("Ngày khai trương")
    @Column(name = "opening_date", nullable = false)
    @NotNull
    private LocalDate openingDate;

    @Comment("Mã số thuế")
    @Column(name = "tax_code", length = 20)
    @NotNull
    private String taxCode;

    @Comment("Thời gian mở cửa")
    @Column(name = "opening_time", nullable = false)
    @NotNull
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    @NotNull
    private LocalTime closingTime;
    
    
    public boolean changeStoreName(StoreName storeName){
        if (this.name.equals(storeName)){
            return false;
        }
        
        this.name = storeName;
        return true;
    }
    
    public boolean changeAddress(Address address){
        if (this.address.equals(address)){
            return false;
        }
        
        this.address = address;
        return true;
    }
    
    public boolean changePhoneNumber(PhoneNumber phoneNumber){
        if (this.phone.equals(phoneNumber)){
            return false;
        }
        
        this.phone = phoneNumber;
        return true;
    }
    
    public boolean changeEmail(Email email){
        if (this.email.equals(email)){
            return false;
        }
        
        this.email = email;
        return true;
    }
    
    public boolean changeTaxCode(String taxCode){
        if (this.taxCode.equals(taxCode)){
            return false;
        }
        
        this.taxCode = taxCode;
        return true;
    }
    
    public boolean changeOpeningTime(LocalTime openingTime){
        if (this.openingTime.equals(openingTime)){
            return false;
        }
        
        this.openingTime = openingTime;
        return true;
    }
    
    public boolean changeClosingTime(LocalTime closingTime){
        if (this.closingTime.equals(closingTime)){
            return false;
        }
        
        this.closingTime = closingTime;
        return true;
    }
    
    public boolean changeOpeningDate(LocalDate openingDate){
        if (this.openingDate.equals(openingDate)){
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
        StoreEntity that = (StoreEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}