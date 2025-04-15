package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.UpdateMemberForCustomer;
import com.mts.backend.domain.customer.CustomerEntity;
import com.mts.backend.domain.customer.MembershipTypeEntity;
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
        
        CustomerEntity customer = mustExistCustomer(command.getCustomerId());
        MembershipTypeEntity membershipType = mustExistMembershipType(command.getMemberId());
        
        if (customer.changeMembershipType(membershipType)) {
            customer.changeRewardPoint(RewardPoint.builder().
                    value(membershipType.getRequiredPoint())
                    .build());
        }
        
        var updatedCustomer = customerRepository.save(customer);
        
        return CommandResult.success(updatedCustomer.getId());
    }
    
    private CustomerEntity mustExistCustomer(CustomerId customerId) {
        return customerRepository.findById(customerId.getValue())
                .orElseThrow(() -> new NotFoundException("Khách hàng không tồn tại"));
    }
    
    private MembershipTypeEntity mustExistMembershipType(MembershipTypeId membershipTypeId) {
        Objects.requireNonNull(membershipTypeId, "Membership type id is required");
        
        if (!membershipTypeRepository.existsById(membershipTypeId.getValue())) {
            throw new NotFoundException("Loại thành viên không tồn tại");
        }
        
        return membershipTypeRepository.getReferenceById(membershipTypeId.getValue());
    }
    
}
