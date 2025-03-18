package com.mts.backend.domain.store;

import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.store.identifier.StoreId;
import com.mts.backend.domain.store.value_object.Address;
import com.mts.backend.domain.store.value_object.StoreName;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class Store extends AbstractAggregateRoot<StoreId> {
    @NonNull
    private StoreName storeName;
    @NonNull
    private Address address;
    @NonNull
    private PhoneNumber phoneNumber;
    @NonNull
    private String taxCode;
    @NonNull
    private Email email;
    @NonNull
    private LocalTime openTime;
    @NonNull
    private LocalTime closeTime;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Store(StoreId id,
                 @NonNull StoreName storeName,
                 @NonNull Address address,
                 @NonNull PhoneNumber phoneNumber,
                 @NonNull Email email,
                 @NonNull String taxCode,
                 @NonNull LocalTime openTime,
                 @NonNull LocalTime closeTime,
                 LocalDateTime updatedAt){
        super(id);
        this.storeName = Objects.requireNonNull(storeName, "Store name is required");
        this.address = Objects.requireNonNull(address, "Address is required");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number is required");
        this.taxCode = Objects.requireNonNull(taxCode, "Tax code is required");
        this.email = Objects.requireNonNull(email, "Email is required");
        this.openTime = Objects.requireNonNull(openTime, "Open time is required");
        this.closeTime = Objects.requireNonNull(closeTime, "Close time is required");
        this.createdAt = LocalDateTime.now();
        this.updatedAt = updatedAt;
    }
    
    public boolean changeStoreName(StoreName storeName){
        if (this.storeName.equals(storeName)){
            return false;
        }
        
        this.storeName = storeName;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeAddress(Address address){
        if (this.address.equals(address)){
            return false;
        }
        
        this.address = address;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    
    public boolean changePhoneNumber(PhoneNumber phoneNumber){
        if (this.phoneNumber.equals(phoneNumber)){
            return false;
        }
        
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeTaxCode(String taxCode){
        if (this.taxCode.equals(taxCode)){
            return false;
        }
        
        this.taxCode = taxCode;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeOpenTime(LocalTime openTime){
        if (this.openTime.equals(openTime)){
            return false;
        }
        
        this.openTime = openTime;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeCloseTime(LocalTime closeTime){
        if (this.closeTime.equals(closeTime)){
            return false;
        }
        
        this.closeTime = closeTime;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public StoreName getStoreName(){
        return storeName;
    }
    
    public Address getAddress(){
        return address;
    }
    
    public PhoneNumber getPhoneNumber(){
        return phoneNumber;
    }
    
    public String getTaxCode(){
        return taxCode;
    }
    
    public LocalTime getOpenTime(){
        return openTime;
    }
    
    public LocalTime getCloseTime(){
        return closeTime;
    }
    
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }


    public boolean changeEmail(Email email){
        if (this.email.equals(email)){
            return false;
        }
        
        this.email = email;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public Email getEmail(){
        return email;
    }
}
