package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.DeleteCusByIdCommand;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCusByIdCommandHandler implements ICommandHandler<DeleteCusByIdCommand, CommandResult> {

    private final JpaCustomerRepository customerRepository;

    public DeleteCusByIdCommandHandler(JpaCustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    @Override
    @Transactional
    public CommandResult handle(DeleteCusByIdCommand command) {
        Objects.requireNonNull(command, "DeleteCusByIdCommand is null");

        var customer = customerRepository.findById(command.getCustomerId().getValue())
            .orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng với id: " + command.getCustomerId()));

        customerRepository.delete(customer);

        return CommandResult.success(customer.getId());
    }
}