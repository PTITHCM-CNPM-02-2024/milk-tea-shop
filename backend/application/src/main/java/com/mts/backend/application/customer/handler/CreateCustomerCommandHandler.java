package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.CreateCustomerCommand;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.CustomerEntity;
import com.mts.backend.domain.customer.MembershipTypeEntity;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.RewardPoint;
import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateCustomerCommandHandler implements ICommandHandler<CreateCustomerCommand, CommandResult> {
    private final JpaCustomerRepository customerRepository;
    private final JpaAccountRepository accountRepository;
    private final JpaMembershipTypeRepository membershipTypeRepository;
    
    public CreateCustomerCommandHandler(
            JpaCustomerRepository customerRepository,
            JpaAccountRepository accountRepository,
            JpaMembershipTypeRepository membershipTypeRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.membershipTypeRepository = membershipTypeRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(CreateCustomerCommand command) {
        Objects.requireNonNull(command, "Create customer command is required");
        
        PhoneNumber phoneNumber = command.getPhone();
        verifyUniquePhoneNumber(phoneNumber);
        
        command.getEmail().ifPresent(this::mustUniqueEmail);
        
        AccountEntity acEn     = verifyAccount(command.getAccountId().orElse(null));  
        
        MembershipTypeEntity msEn = verifyMemberShip(command.getMembershipId().orElse(null));

        CustomerEntity cus = CustomerEntity.builder()
                .id(CustomerId.create().getValue())
                .firstName(command.getFirstName().orElse(null))
                .lastName(command.getLastName().orElse(null))
                .email(command.getEmail().orElse(null))
                .gender(command.getGender().orElse(null))
                .phone(command.getPhone())
                .membershipTypeEntity(msEn)
                .accountEntity(acEn)
                .currentPoints(RewardPoint.builder().value(msEn.getRequiredPoint()).build()
                )
                .build();
        
        var createdCustomer = customerRepository.save(cus);
        
        return CommandResult.success(createdCustomer.getId());
        
    }
    
    private AccountEntity verifyAccount(AccountId id){
        if (id == null){
            return null;
        }
        
        if (customerRepository.existsByAccountEntity_Id(id.getValue())){
            throw new DuplicateException("Tài khoản" + id.getValue() + " đã tồn tại");
        }
        
        if (!accountRepository.existsById(id.getValue())){
            throw new NotFoundException("Tài khoản không tồn tại");
        }
        
        return accountRepository.getReferenceById(id.getValue());
        
    }
    
    private MembershipTypeEntity verifyMemberShip(MembershipTypeId id){
        if (id == null){
            return membershipTypeRepository.findWithMinimumRequiredPoint().orElseThrow(() -> new NotFoundException(
                    "Không tồn tại một membership type nào"));
        }
        
        if (!membershipTypeRepository.existsById(id)){
            throw new NotFoundException("Membership type không tồn tại");
        }
        
        return membershipTypeRepository.getReferenceById(id) ;
    }
    
    private void verifyUniquePhoneNumber(PhoneNumber phoneNumber){
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        if (customerRepository.existsByPhone(phoneNumber)){
            throw new NotFoundException("Số điện thoại đã tồn tại");
        }
    }
    
    private void mustUniqueEmail(Email email){
        Objects.requireNonNull(email, "Email is required");
        if (customerRepository.existsByEmail(email)){
            throw new NotFoundException("Email đã tồn tại");
        }
    }
}
