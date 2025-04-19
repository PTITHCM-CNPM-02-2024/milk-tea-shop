package com.mts.backend.application.staff.query_handler;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.mts.backend.application.staff.query.GetEmpByAccountIdQuery;
import com.mts.backend.application.staff.response.EmployeeDetailResponse;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;

@Service
public class GetEmpByAccountIdQueryHandler implements IQueryHandler<GetEmpByAccountIdQuery, CommandResult> {
    private final JpaEmployeeRepository jpaEmployeeRepository;

    public GetEmpByAccountIdQueryHandler(JpaEmployeeRepository jpaEmployeeRepository) {
        this.jpaEmployeeRepository = jpaEmployeeRepository;
    }

    @Override
    public CommandResult handle(GetEmpByAccountIdQuery query) {
        Objects.requireNonNull(query, "Query cannot be null");

        var employee = jpaEmployeeRepository.findByAccountEntity_IdFetch(query.getAccountId().getValue()).orElseThrow(() -> new NotFoundException("Nhân viên không tồn tại"));

        var response = EmployeeDetailResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName().getValue())
                .lastName(employee.getLastName().getValue())
                .email(employee.getEmail().getValue())
                .phone(employee.getPhone().getValue())
                .gender(employee.getGender().toString())
                .position(employee.getPosition().getValue())
                .accountId(employee.getAccount().getId())
                .build();

        return CommandResult.success(response);
    }
}