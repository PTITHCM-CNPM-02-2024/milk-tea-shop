package com.mts.backend.api.staff.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.staff.request.CreateManagerRequest;
import com.mts.backend.api.staff.request.UpdateManagerRequest;
import com.mts.backend.application.staff.ManagerCommandBus;
import com.mts.backend.application.staff.ManagerQueryBus;
import com.mts.backend.application.staff.command.CreateManagerCommand;
import com.mts.backend.application.staff.command.UpdateManagerCommand;
import com.mts.backend.application.staff.query.DefaultManagerQuery;
import com.mts.backend.application.staff.query.ManagerByIdQuery;
import com.mts.backend.application.staff.response.ManagerDetailResponse;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.staff.identifier.ManagerId;
import com.mts.backend.shared.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<Long>> createManager(@RequestBody CreateManagerRequest request) {
        var command = CreateManagerCommand.builder()
                .email(Email.builder().value(request.getEmail()).build())
                .firstName(FirstName.builder().value(request.getFirstName()).build())
                .lastName(LastName.builder().value(request.getLastName()).build())
                .accountId(AccountId.of(request.getAccountId()))
                .gender(Gender.valueOf(request.getGender()))
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long) result.getData())) : handleError(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updateManager(@PathVariable("id") Long id, @RequestBody UpdateManagerRequest request) {
        var command = UpdateManagerCommand.builder()
                .id(ManagerId.of(id))
                .email(Email.builder().value(request.getEmail()).build())
                .firstName(FirstName.builder().value(request.getFirstName()).build())
                .lastName(LastName.builder().value(request.getLastName()).build())
                .gender(Gender.valueOf(request.getGender()))
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long) result.getData())) : handleError(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getManager(@PathVariable("id") Long id) {
        var query = ManagerByIdQuery.builder()
                .id(ManagerId.of(id)).build();

        var result = queryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((ManagerDetailResponse) result.getData())) : handleError(result);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getManagers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        var request = DefaultManagerQuery.builder()
                .page(page)
                .size(size)
                .build();

        var result = queryBus.dispatch(request);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
}
