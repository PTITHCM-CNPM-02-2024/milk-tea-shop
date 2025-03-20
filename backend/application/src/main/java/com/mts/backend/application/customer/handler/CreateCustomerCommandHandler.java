package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.CreateCustomerCommand;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.repository.IAccountRepository;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.repository.ICustomerRepository;
import com.mts.backend.domain.customer.repository.IMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.RewardPoint;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateCustomerCommandHandler implements ICommandHandler<CreateCustomerCommand, CommandResult> {
    private final ICustomerRepository customerRepository;
    private final IAccountRepository accountRepository;
    private final IMembershipTypeRepository membershipTypeRepository;
    
    public CreateCustomerCommandHandler(ICustomerRepository customerRepository, IAccountRepository accountRepository, IMembershipTypeRepository membershipTypeRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.membershipTypeRepository = membershipTypeRepository;
    }
    @Override
    public CommandResult handle(CreateCustomerCommand command) {
        Objects.requireNonNull(command, "Create customer command is required");
        
        PhoneNumber phoneNumber = PhoneNumber.of(command.getPhone());
        verifyUniquePhoneNumber(phoneNumber);
        
        Email email = command.getEmail().map(Email::of).orElse(null);
        mustUniqueEmailIfSpecific(email);
        
        AccountId accountId = command.getAccountId().map(AccountId::of).orElse(null);
        mustExistAccountIfPresent(accountId);
        
        Gender gender = command.getGender().map(Gender::valueOf).orElse(null);
        
        MembershipTypeId membershipTypeId = mustExistMembershipType(MembershipTypeId.of(command.getMembershipId()));

        Customer cus = new Customer(
                CustomerId.create(),
                command.getFirstName().map(FirstName::of).orElse(null),
                command.getLastName().map(LastName::of).orElse(null),
                phoneNumber,
                email,
                gender,
                RewardPoint.of(0),
                membershipTypeId,
                accountId,
                LocalDateTime.now());
        
        var savedCustomer     = customerRepository.save(cus);
        
        return CommandResult.success(savedCustomer.getId().getValue());
    }
    
    private void mustExistAccountIfPresent(AccountId id){
        if (id != null && !accountRepository.existsById(id)){
            throw new NotFoundException("Account không tồn tại");
        }
    }
    
    private MembershipTypeId mustExistMembershipType(MembershipTypeId id){
        Objects.requireNonNull(id, "Membership type id is required");
        return membershipTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Membership type không tồn tại")).getId();
    }
    
    private void verifyUniquePhoneNumber(PhoneNumber phoneNumber){
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        if (customerRepository.existsByPhone(phoneNumber)){
            throw new NotFoundException("Số điện thoại đã tồn tại");
        }
    }
    
    private void mustUniqueEmailIfSpecific(Email email){
        if (email != null && customerRepository.existsByEmail(email)){
            throw new NotFoundException("Email đã tồn tại");
        }
    }
}
