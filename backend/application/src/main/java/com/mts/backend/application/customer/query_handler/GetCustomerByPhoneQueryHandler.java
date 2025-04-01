package com.mts.backend.application.customer.query_handler;

import com.mts.backend.application.customer.query.CustomerByPhoneQuery;
import com.mts.backend.application.customer.response.CustomerDetailResponse;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.FirstName;
import com.mts.backend.domain.common.value_object.LastName;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.customer.CustomerEntity;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetCustomerByPhoneQueryHandler implements IQueryHandler<CustomerByPhoneQuery, CommandResult> {

    private final JpaCustomerRepository customerRepository;
    
    public GetCustomerByPhoneQueryHandler(JpaCustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(CustomerByPhoneQuery query) {
        Objects.requireNonNull(query, "Get customer by phone query is required");
        
        CustomerEntity customer = mustExistCustomer(query.getPhone());

        CustomerDetailResponse response = CustomerDetailResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName().map(FirstName::getValue).orElse(null))
                .lastName(customer.getLastName().map(LastName::getValue).orElse(null))
                .email(customer.getEmail().map(Email::getValue).orElse(null))
                .phone(customer.getPhone().getValue())
                .membershipId(customer.getMembershipTypeEntity().getId())
                .rewardPoint(customer.getCurrentPoints().getValue())
                .accountId(customer.getAccountEntity().map(a -> a.getId()).orElse(null))
                .build();
        
        return CommandResult.success(response);
    }
    
    private CustomerEntity mustExistCustomer(PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        return customerRepository.findByPhone(phoneNumber)
                .orElseThrow(() -> new NotFoundException("Khách hàng không tồn tại"));
    }
    
}
