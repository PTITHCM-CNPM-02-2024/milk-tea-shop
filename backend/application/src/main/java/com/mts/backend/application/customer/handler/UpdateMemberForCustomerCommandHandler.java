package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.UpdateMemberForCustomer;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.customer.MembershipType;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.RewardPoint;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateMemberForCustomerCommandHandler implements ICommandHandler<UpdateMemberForCustomer, CommandResult> {
    private final JpaCustomerRepository customerRepository;
    private final JpaMembershipTypeRepository membershipTypeRepository;

    public UpdateMemberForCustomerCommandHandler(JpaCustomerRepository customerRepository, JpaMembershipTypeRepository membershipTypeRepository) {
        this.customerRepository = customerRepository;
        this.membershipTypeRepository = membershipTypeRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(UpdateMemberForCustomer command) {

        Customer customer = mustExistCustomer(command.getCustomerId());
        MembershipType membershipType = membershipTypeRepository.findById(command.getMemberId().getValue())
                .orElseThrow(() -> new NotFoundException("Loại thành viên không tồn tại"));

        if (customer.setMembershipType(membershipType)) {
            customer.setCurrentPoint(RewardPoint.of(membershipType.getRequiredPoint()));
        }

        return CommandResult.success(customer.getId());
    }

    private Customer mustExistCustomer(CustomerId customerId) {
        return customerRepository.findById(customerId.getValue())
                .orElseThrow(() -> new NotFoundException("Khách hàng không tồn tại"));
    }
}
