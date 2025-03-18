package com.mts.backend.application.customer.query_handler;

import com.mts.backend.application.customer.query.DefaultCustomerQuery;
import com.mts.backend.application.customer.response.CustomerDetailResponse;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.FirstName;
import com.mts.backend.domain.common.value_object.LastName;
import com.mts.backend.domain.customer.repository.ICustomerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllCustomerQueryHandler implements IQueryHandler<DefaultCustomerQuery, CommandResult> {
    private final ICustomerRepository customerRepository;

    public GetAllCustomerQueryHandler(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CommandResult handle(DefaultCustomerQuery query) {
        Objects.requireNonNull(query, "Default customer query is required");

        var customers = customerRepository.findAll();

        List<CustomerDetailResponse> responses = new ArrayList<>();

        customers.forEach(customer -> {
            responses.add(CustomerDetailResponse.builder()
                    .id(customer.getId().getValue())
                    .firstName(customer.getFirstName().map(FirstName::getValue).orElse(null))
                    .lastName(customer.getLastName().map(LastName::getValue).orElse(null))
                    .email(customer.getEmail().map(Email::getValue).orElse(null))
                    .phone(customer.getPhoneNumber().getValue())
                    .membershipId(customer.getMembershipTypeId().getValue())
                    .rewardPoint(customer.getRewardPoint().getValue())
                    .accountId(customer.getAccountId().map(AccountId::getValue).orElse(null))
                    .build());
        });

        return CommandResult.success(responses);
    }
}
