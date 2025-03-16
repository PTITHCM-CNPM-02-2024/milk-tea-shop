package com.mts.backend.domain.staff;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.staff.identifier.ManagerId;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;

public class Manager extends AbstractAggregateRoot<ManagerId> {
    private final LocalDateTime createdAt = LocalDateTime.now();
    @NonNull
    private FirstName firstName;
    @NonNull
    private LastName lastName;
    @NonNull
    private Email email;
    @NonNull
    private Gender gender;
    @NonNull
    private PhoneNumber phoneNumber;
    @NonNull
    private AccountId accountId;
    private LocalDateTime updatedAt;

    public Manager(@NonNull ManagerId managerId, @NonNull FirstName firstName, @NonNull LastName lastName, @NonNull Email email, @NonNull PhoneNumber phoneNumber, @NonNull Gender gender, @NonNull AccountId accountId, @Nullable LocalDateTime updatedAt) {

        super(managerId);

        this.firstName = Objects.requireNonNull(firstName, "First name is required");

        this.lastName = Objects.requireNonNull(lastName, "Last name is required");
        this.email = Objects.requireNonNull(email, "Email is required");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number is required");
        this.accountId = Objects.requireNonNull(accountId, "Account id is required");
        this.gender = Objects.requireNonNull(gender, "Gender is required");
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }
    
    
    public FirstName getFirstName() {
        return firstName;
    }
    
    public LastName getLastName() {
        return lastName;
    }
    
    public Email getEmail() {
        return email;
    }
    
    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }
    
    public AccountId getAccountId() {
        return accountId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public Gender getGender() {
        return gender;
    }

    public boolean changeFirstName(FirstName firstName) {
        Objects.requireNonNull(firstName, "First name is required");
        if (this.firstName.equals(firstName)) {
            return false;
        }
        this.firstName = firstName;
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    public boolean changeLastName(LastName lastName) {
        Objects.requireNonNull(lastName, "Last name is required");
        if (this.lastName.equals(lastName)) {
            return false;
        }
        this.lastName = lastName;
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    public boolean changeEmail(Email email) {
        Objects.requireNonNull(email, "Email is required");
        if (this.email.equals(email)) {
            return false;
        }
        this.email = email;
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    public boolean changePhoneNumber(PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        if (this.phoneNumber.equals(phoneNumber)) {
            return false;
        }
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    public boolean changeGender(Gender gender) {
        Objects.requireNonNull(gender, "gender is required");
        if (this.gender.equals(gender)) {
            return false;
        }

        this.gender = gender;
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    public boolean changeAccountId(AccountId accountId) {
        Objects.requireNonNull(accountId, "Account id is required");
        if (this.accountId.equals(accountId)) {
            return false;
        }
        this.accountId = accountId;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
}
