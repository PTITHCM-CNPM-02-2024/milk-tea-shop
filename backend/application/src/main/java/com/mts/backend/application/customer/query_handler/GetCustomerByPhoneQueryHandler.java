package com.mts.backend.application.customer.query_handler;

import com.mts.backend.application.customer.query.CustomerByPhoneQuery;
import com.mts.backend.application.customer.response.CustomerDetailResponse;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.FirstName;
import com.mts.backend.domain.common.value_object.LastName;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.customer.repository.ICustomerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetCustomerByPhoneQueryHandler implements IQueryHandler<CustomerByPhoneQuery, CommandResult> {

    private final ICustomerRepository customerRepository;
    
    public GetCustomerByPhoneQueryHandler(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Override
    public CommandResult handle(CustomerByPhoneQuery query) {
        Objects.requireNonNull(query, "Get customer by phone query is required");
        
        Customer customer = mustExistCustomer(PhoneNumber.of(query.getPhone()));

        CustomerDetailResponse response = CustomerDetailResponse.builder()
                .id(customer.getId().getValue())
                .firstName(customer.getFirstName().map(FirstName::getValue).orElse(null))
                .lastName(customer.getLastName().map(LastName::getValue).orElse(null))
                .phone(customer.getPhoneNumber().getValue())
                .email(customer.getEmail().map(Email::getValue).orElse(null))
                .gender(customer.getGender().map(Enum::name).orElse(null))
                .rewardPoint(customer.getRewardPoint().getValue())
                .membershipId(customer.getMembershipTypeId().getValue())
                .accountId(customer.getAccountId().map(AccountId::getValue).orElse(null))
                .build();
        
        return CommandResult.success(response);
    }
    
    private Customer mustExistCustomer(PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        return customerRepository.findByPhone(phoneNumber)
                .orElseThrow(() -> new NotFoundException("Khách hàng không tồn tại"));
    }
    
}
