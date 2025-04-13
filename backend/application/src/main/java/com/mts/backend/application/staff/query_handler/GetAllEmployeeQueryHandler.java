package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.staff.query.DefaultEmployeeQuery;
import com.mts.backend.application.staff.response.EmployeeDetailResponse;
import com.mts.backend.domain.staff.EmployeeEntity;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetAllEmployeeQueryHandler implements IQueryHandler<DefaultEmployeeQuery, CommandResult> {
    private final JpaEmployeeRepository employeeRepository;

    public GetAllEmployeeQueryHandler(JpaEmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public CommandResult handle(DefaultEmployeeQuery query) {
        Objects.requireNonNull(query);

        Page<EmployeeEntity> employees = employeeRepository.findAllWithJoinFetch(PageRequest.of(query.getPage(), query.getSize()));
        Page<EmployeeDetailResponse> responses = employees.map( emp -> {
            return EmployeeDetailResponse.builder()
                    .id(emp.getId())
                    .firstName(emp.getFirstName().getValue())
                    .lastName(emp.getLastName().getValue())
                    .email(emp.getEmail().getValue())
                    .phone(emp.getPhone().getValue())
                    .gender(emp.getGender().toString())
                    .position(emp.getPosition().getValue())
                    .accountId(emp.getAccountEntity().getId())
                    .build();
        });


        return CommandResult.success(responses);
    }
}
