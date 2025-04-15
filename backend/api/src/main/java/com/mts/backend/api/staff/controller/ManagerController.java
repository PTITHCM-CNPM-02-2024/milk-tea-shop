package com.mts.backend.api.staff.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.staff.request.CreateManagerRequest;
import com.mts.backend.api.staff.request.UpdateManagerRequest;
import com.mts.backend.application.report.ReportQueryBus;
import com.mts.backend.application.staff.ManagerCommandBus;
import com.mts.backend.application.staff.ManagerQueryBus;
import com.mts.backend.application.staff.command.CreateManagerCommand;
import com.mts.backend.application.staff.command.UpdateManagerCommand;
import com.mts.backend.application.staff.query.DefaultManagerQuery;
import com.mts.backend.application.staff.query.GetManagerByAccountIdQuery;
import com.mts.backend.application.staff.query.ManagerByIdQuery;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.staff.identifier.ManagerId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/managers")
public class ManagerController implements IController {
    private final ManagerCommandBus commandBus;
    private final ManagerQueryBus queryBus;

    public ManagerController(ManagerCommandBus commandBus, ManagerQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @PostMapping
    public ResponseEntity<?> createManager(@RequestBody CreateManagerRequest request) {
        var command = CreateManagerCommand.builder()
                .email(Email.builder().value(request.getEmail()).build())
                .firstName(FirstName.builder().value(request.getFirstName()).build())
                .lastName(LastName.builder().value(request.getLastName()).build())
                .gender(Gender.valueOf(request.getGender()))
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                .username(Username.builder().value(request.getUsername()).build())
                .password(PasswordHash.builder().value(request.getPassword()).build())
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateManager(@PathVariable("id") Long id, @RequestBody UpdateManagerRequest request) {
        var command = UpdateManagerCommand.builder()
                .id(ManagerId.of(id))
                .email(Email.builder().value(request.getEmail()).build())
                .firstName(FirstName.builder().value(request.getFirstName()).build())
                .lastName(LastName.builder().value(request.getLastName()).build())
                .gender(Gender.valueOf(request.getGender()))
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getManager(@PathVariable("id") Long id) {
        var query = ManagerByIdQuery.builder()
                .id(ManagerId.of(id)).build();

        var result = queryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @GetMapping
    public ResponseEntity<?> getManagers(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var request = DefaultManagerQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = queryBus.dispatch(request);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<?> getManagerByAccountId(@PathVariable("id") Long id) {
        var query = GetManagerByAccountIdQuery.builder().accountId(AccountId.of(id)).build();
        
        var result = queryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
