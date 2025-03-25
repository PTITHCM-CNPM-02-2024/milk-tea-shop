package com.mts.backend.domain.customer;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.value_object.RewardPoint;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Customer extends AbstractAggregateRoot<CustomerId> {
    @Nullable
    private FirstName firstName;
    @Nullable
    private LastName lastName;
    @NonNull
    private PhoneNumber phoneNumber;
    @Nullable
    private Email email;
    @Nullable
    private Gender gender;
    @NonNull
    private RewardPoint rewardPoint;
    @NonNull
    private MembershipTypeId membershipTypeId;
    @Nullable
    private final AccountId accountId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Customer(CustomerId id,
                    @Nullable FirstName firstName,
                    @Nullable LastName lastName,
                    @NonNull PhoneNumber phoneNumber,
                    @Nullable Email email,
                    @Nullable Gender gender,
                    RewardPoint rewardPoint,
                    MembershipTypeId membershipTypeId,
                    @Nullable AccountId accountId,
                    LocalDateTime updatedAt){
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.gender = gender;
        this.rewardPoint = Objects.requireNonNull(rewardPoint, "Reward point is required");
        this.membershipTypeId = Objects.requireNonNull(membershipTypeId, "Membership type id is required");
        this.accountId = accountId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = updatedAt;
    }
    
    public boolean changeFirstName(FirstName firstName){
        if (Objects.equals(this.firstName, firstName)){
            return false;
        }
        
        this.firstName = firstName;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeLastName(LastName lastName){
        if (Objects.equals(this.lastName, lastName)){
            return false;
        }
        this.lastName = lastName;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changePhoneNumber(PhoneNumber phoneNumber){
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        if (this.phoneNumber.equals(phoneNumber)){
            return false;
        }
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeEmail(Email email){
        if (Objects.equals(this.email, email)){
            return false;
        }
        this.email = email;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeGender(Gender gender){
        if (Objects.equals(this.gender, gender)){
            return false;
        }
        this.gender = gender;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeRewardPoint(RewardPoint rewardPoint){
        Objects.requireNonNull(rewardPoint, "Reward point is required");
        if (this.rewardPoint.equals(rewardPoint)){
            return false;
        }
        this.rewardPoint = rewardPoint;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public Optional<FirstName> getFirstName(){
        return Optional.ofNullable(firstName);
    }
    
    public Optional<LastName> getLastName(){
        return Optional.ofNullable(lastName);
    }
    
    public PhoneNumber getPhoneNumber(){
        return phoneNumber;
    }
    
    public Optional<Email> getEmail(){
        return Optional.ofNullable(email);
    }
    
    public Optional<Gender> getGender(){
        return Optional.ofNullable(gender);}
    
    public RewardPoint getRewardPoint(){
        return rewardPoint;
    }
    
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }
    
    public MembershipTypeId getMembershipTypeId(){
        return membershipTypeId;
    }
    
    public boolean changeMembershipTypeId(MembershipTypeId membershipTypeId){
        Objects.requireNonNull(membershipTypeId, "Membership type id is required");
        if (this.membershipTypeId.equals(membershipTypeId)){
            return false;
        }
        this.membershipTypeId = membershipTypeId;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean addRewardPoint(RewardPoint rewardPoint){
        Objects.requireNonNull(rewardPoint, "Reward point is required");
        this.rewardPoint = this.rewardPoint.add(rewardPoint);
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean subtractRewardPoint(RewardPoint rewardPoint){
        Objects.requireNonNull(rewardPoint, "Reward point is required");
        this.rewardPoint = this.rewardPoint.subtract(rewardPoint);
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public Optional<AccountId> getAccountId(){
        return Optional.ofNullable(accountId);
    }
    
    public String getFullName(){
        if (firstName == null && lastName == null){
            return "";
        }
        
        return firstName + " " + lastName;
    }
    
}
