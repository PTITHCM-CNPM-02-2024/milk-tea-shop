package com.mts.backend.application.customer.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateCustomerCommand implements ICommand<CommandResult> {
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String phone;
    private Integer membershipId;
    private Long accountId;
}
