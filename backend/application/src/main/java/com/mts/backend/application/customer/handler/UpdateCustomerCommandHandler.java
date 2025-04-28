package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.UpdateCustomerCommand;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
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

        try {
            Customer customer = mustExistCustomer(command.getId());

            customer.setFirstName(command.getFirstName().orElse(null));
            customer.setLastName(command.getLastName().orElse(null));
            customer.setGender(command.getGender().orElse(null));
            customer.setPhone(command.getPhone());
            customer.changeEmail(command.getEmail().orElse(null));

            customerRepository.saveAndFlush(customer);
            return CommandResult.success(customer.getId());
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_customer_phone")) {
                throw new DomainException("Số điện thoại đã tồn tại");
            }
            if (e.getMessage().contains("uk_customer_email")) {
                throw new DomainException("Email đã tồn tại");
            }
            throw new DomainException("Lỗi khi cập nhật khách hàng", e);
        }
    }

    private Customer mustExistCustomer(CustomerId customerId) {
        Objects.requireNonNull(customerId, "Customer id is required");
        return customerRepository.findById(customerId.getValue())
                .orElseThrow(() -> new NotFoundException("Khách hàng không tồn tại"));
    }

}
