package com.mts.backend.application.customer.command;

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
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String phone;
    
    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }
    
    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }
    
    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }
    
    public Optional<String> getGender() {
        return Optional.ofNullable(gender);
    }
    
}
