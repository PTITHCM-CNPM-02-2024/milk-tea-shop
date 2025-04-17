package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.UpdateCustomerCommand;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.CustomerEntity;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateCustomerCommandHandler implements ICommandHandler<UpdateCustomerCommand, CommandResult> {
    private final JpaCustomerRepository customerRepository;

    public UpdateCustomerCommandHandler(JpaCustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(UpdateCustomerCommand command) {
        Objects.requireNonNull(command, "Update customer command is required");
        
        CustomerEntity customer = mustExistCustomer(command.getId());
        
        if (customer.changeEmail(command.getEmail().orElse(null))) {
            verifyUniqueEmail(command.getId(), command.getEmail().orElse(null));
        }
        
        if (customer.changePhone(command.getPhone())) {
            verifyUniquePhoneNumber(command.getId(), command.getPhone());
        }
        
        customer.changeFirstName(command.getFirstName().orElse(null));
        customer.changeLastName(command.getLastName().orElse(null));
        customer.changeGender(command.getGender().orElse(null));
        
        var updatedCustomer = customerRepository.save(customer);
        
        return CommandResult.success(updatedCustomer.getId());
    }
    
    private CustomerEntity mustExistCustomer(CustomerId customerId) {
        Objects.requireNonNull(customerId, "Customer id is required");
        return customerRepository.findById(customerId.getValue())
                .orElseThrow(() -> new NotFoundException("Khách hàng không tồn tại"));
    }
    private void verifyUniquePhoneNumber(CustomerId id , PhoneNumber phoneNumber) {
        Objects.requireNonNull(id, "Customer id is required");
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        if (customerRepository.existsByIdNotAndPhone(id.getValue(), phoneNumber)) {
            throw new DuplicateException("Số điện thoại đã tồn tại");
        }
    }
    
    private void verifyUniqueEmail(CustomerId id, Email email) {
        if (email == null) {
            return;
        }
        if (customerRepository.existsByIdNotAndEmail(id.getValue(), email)) {
            throw new DuplicateException("Email đã tồn tại");
        }
    }
}
