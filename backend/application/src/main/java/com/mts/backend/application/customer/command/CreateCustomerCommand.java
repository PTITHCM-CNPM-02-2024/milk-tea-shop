package com.mts.backend.application.customer.command;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateCustomerCommand implements ICommand<CommandResult> {
    private FirstName firstName;
    private LastName lastName;
    private Email email;
    private Gender gender;
    private PhoneNumber phone;
    private MembershipTypeId membershipId;
    private AccountId accountId;
    
    public Optional<FirstName> getFirstName() {
        return Optional.ofNullable(firstName);
    }
    
    public Optional<LastName> getLastName() {
        return Optional.ofNullable(lastName);
    }
    
    public Optional<Email> getEmail() {
        return Optional.ofNullable(email);
    }
    
    
    public Optional<MembershipTypeId> getMembershipId() {
        return Optional.ofNullable(membershipId);
    }
    
    public Optional<AccountId> getAccountId() {
        return Optional.ofNullable(accountId);
    }
    
    public Optional<Gender> getGender(){
        return Optional.of(gender);
    }
    
}
