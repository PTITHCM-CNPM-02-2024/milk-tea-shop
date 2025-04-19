package com.mts.backend.application.customer.query_handler;

import com.mts.backend.application.customer.query.GetCusByAccountIdQuery;
import com.mts.backend.application.customer.response.CustomerDetailResponse;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.FirstName;
import com.mts.backend.domain.common.value_object.LastName;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetCusByAccountIdQueryHandler implements IQueryHandler<GetCusByAccountIdQuery, CommandResult> {
    private final JpaCustomerRepository jpaCustomerRepository;

    public GetCusByAccountIdQueryHandler(JpaCustomerRepository jpaCustomerRepository) {
        this.jpaCustomerRepository = jpaCustomerRepository;
    }
    
    @Override
    public CommandResult handle(GetCusByAccountIdQuery query) {
        Objects.requireNonNull(query, "Query cannot be null");
        var customer =
                jpaCustomerRepository.findByAccountEntity_Id(query.getAccountId().getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy thông tin khách hàng"));
        
        
        var response = CustomerDetailResponse.builder()
                .id(customer.getId())
                .email(customer.getEmail().map(Email::getValue).orElse(null))
                .firstName(customer.getFirstName().map(FirstName::getValue).orElse(null))
                .lastName(customer.getLastName().map(LastName::getValue).orElse(null))
                .gender(customer.getGender().map(Enum::name).orElse(null))
                .membershipId(customer.getMembershipTypeEntity().getId())
                .rewardPoint(customer.getCurrentPoints().getValue())
                .accountId(customer.getAccount().map(Account::getId).orElse(null))
                .phone(customer.getPhone().getValue())
                .gender(customer.getGender().map(Enum::name).orElse(null))
                .build();
        return CommandResult.success(response);
    }
    
    
}
