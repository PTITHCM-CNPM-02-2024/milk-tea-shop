package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.UpdateCustomerCommand;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.repository.ICustomerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateCustomerCommandHandler implements ICommandHandler<UpdateCustomerCommand, CommandResult> {
    private final ICustomerRepository customerRepository;

    public UpdateCustomerCommandHandler(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Override
    public CommandResult handle(UpdateCustomerCommand command) {
        Objects.requireNonNull(command, "Update customer command is required");
        
        Customer customer = mustExistCustomer(CustomerId.of(command.getId()));
        
        if (command.getEmail() != null && customer.changeEmail(Email.of(command.getEmail())) && customer.getEmail().isPresent()) {
            verifyUniqueEmail(customer.getEmail().get());
        }
        
        if (command.getPhone() != null && customer.changePhoneNumber(PhoneNumber.of(command.getPhone()))) {
            verifyUniquePhoneNumber(customer.getPhoneNumber());
        }
        
        customer.changeFirstName(command.getFirstName() != null ? FirstName.of(command.getFirstName()) : null);
        customer.changeLastName(command.getLastName() != null ? LastName.of(command.getLastName()) : null);
        customer.changeGender(command.getGender() != null ? Gender.valueOf(command.getGender()) : null);
        
        var updatedCustomer = customerRepository.save(customer);
        
        return CommandResult.success(updatedCustomer.getId().getValue());
    }
    
    private Customer mustExistCustomer(CustomerId customerId) {
        Objects.requireNonNull(customerId, "Customer id is required");
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Khách hàng không tồn tại"));
    }
    private void verifyUniquePhoneNumber(PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        if (customerRepository.existsByPhone(phoneNumber)) {
            throw new DuplicateException("Số điện thoại đã tồn tại");
        }
    }
    
    private void verifyUniqueEmail(Email email) {
        Objects.requireNonNull(email, "Email is required");
        if (customerRepository.existsByEmail(email)) {
            throw new DuplicateException("Email đã tồn tại");
        }
    }
}
