package com.mts.backend.application.customer.command;

import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateCustomerCommand implements ICommand<CommandResult> {
    private CustomerId id;
    private FirstName firstName;
    private LastName lastName;
    private Email email;
    private Gender gender;
    private PhoneNumber phone;
    
    public Optional<FirstName> getFirstName() {
        return Optional.ofNullable(firstName);
    }
    
    public Optional<LastName> getLastName() {
        return Optional.ofNullable(lastName);
    }
    public Optional<Email> getEmail() {
        return Optional.ofNullable(email);
    }
    
    public Optional<Gender> getGender(){
        return Optional.ofNullable(gender);
    }
    
}
