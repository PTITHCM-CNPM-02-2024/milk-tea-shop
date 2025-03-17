package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.UpdateMemberForCustomer;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.customer.MembershipType;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.repository.ICustomerRepository;
import com.mts.backend.domain.customer.repository.IMembershipTypeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UpdateMemberForCustomerCommandHandler implements ICommandHandler<UpdateMemberForCustomer, CommandResult> {
    private final ICustomerRepository customerRepository;
    private final IMembershipTypeRepository membershipTypeRepository;
    
    public UpdateMemberForCustomerCommandHandler(ICustomerRepository customerRepository, IMembershipTypeRepository membershipTypeRepository) {
        this.customerRepository = customerRepository;
        this.membershipTypeRepository = membershipTypeRepository;
    }
    
    @Override
    public CommandResult handle(UpdateMemberForCustomer command) {
        
        Customer customer = mustExistCustomer(CustomerId.of(command.getCustomerId()));
        MembershipType membershipType = mustExistMembershipType(MembershipTypeId.of(command.getMemberId()));
        
        customer.changeMembershipTypeId(membershipType.getId());
        
        var updatedCustomer = customerRepository.save(customer);
        
        return CommandResult.success(updatedCustomer.getId().getValue());
    }
    
    private Customer mustExistCustomer(CustomerId customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Khách hàng không tồn tại"));
    }
    
    private MembershipType mustExistMembershipType(MembershipTypeId membershipTypeId) {
        return membershipTypeRepository.findById(membershipTypeId)
                .orElseThrow(() -> new NotFoundException("Loại thành viên không tồn tại"));
    }
    
}
