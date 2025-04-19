package com.mts.backend.application.customer.query_handler;

import com.mts.backend.application.customer.query.CustomerByIdQuery;
import com.mts.backend.application.customer.response.CustomerDetailResponse;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.FirstName;
import com.mts.backend.domain.common.value_object.LastName;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetCustomerByIdQueryHandler implements IQueryHandler<CustomerByIdQuery, CommandResult> {
    
    private final JpaCustomerRepository customerRepository;
    
    public GetCustomerByIdQueryHandler(JpaCustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Override
    public CommandResult handle(CustomerByIdQuery query) {
        Objects.requireNonNull(query, "Get customer by id query is required");
        
        Customer customer = mustExistCustomer(query.getId());

        CustomerDetailResponse response = CustomerDetailResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName().map(FirstName::getValue).orElse(null))
                .lastName(customer.getLastName().map(LastName::getValue).orElse(null))
                .email(customer.getEmail().map(Email::getValue).orElse(null))
                .gender(customer.getGender().map(Enum::name).orElse(null))
                .phone(customer.getPhone().getValue())
                .membershipId(customer.getMembershipTypeEntity().getId())
                .rewardPoint(customer.getCurrentPoints().getValue())
                .accountId(customer.getAccount().map(Account::getId).orElse(null))
                .build();
        
        return CommandResult.success(response);
    }
    
    private Customer mustExistCustomer(CustomerId customerId) {
        Objects.requireNonNull(customerId, "Customer id is required");
        return customerRepository.findByIdFetchMembershipTypeAndAccount(customerId.getValue())
                .orElseThrow(() -> new NotFoundException("Khách hàng không tồn tại"));
    }
    
}
