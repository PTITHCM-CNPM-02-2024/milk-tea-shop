package com.mts.backend.application.customer.query_handler;

import com.mts.backend.application.customer.query.DefaultCustomerQuery;
import com.mts.backend.application.customer.response.CustomerDetailResponse;
import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.FirstName;
import com.mts.backend.domain.common.value_object.LastName;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllCustomerQueryHandler implements IQueryHandler<DefaultCustomerQuery, CommandResult> {
    private final JpaCustomerRepository customerRepository;

    public GetAllCustomerQueryHandler(JpaCustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CommandResult handle(DefaultCustomerQuery query) {
        Objects.requireNonNull(query, "Default customer query is required");

        var customers = customerRepository.findAllFetch(Pageable.ofSize(query.getSize())
                .withPage(query.getPage()));

        Page<CustomerDetailResponse> responses = customers.map(customer -> {
            return CustomerDetailResponse.builder()
                    .id(customer.getId())
                    .firstName(customer.getFirstName().map(FirstName::getValue).orElse(null))
                    .lastName(customer.getLastName().map(LastName::getValue).orElse(null))
                    .email(customer.getEmail().map(Email::getValue).orElse(null))
                    .phone(customer.getPhone().getValue())
                    .membershipId(customer.getMembershipTypeEntity().getId())
                    .rewardPoint(customer.getCurrentPoints().getValue())
                    .accountId(customer.getAccountEntity().map(AccountEntity::getId).orElse(null))
                    .build();
        });
        

        return CommandResult.success(responses);
    }
}
