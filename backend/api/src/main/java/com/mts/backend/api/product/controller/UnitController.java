package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.CreateUnitRequest;
import com.mts.backend.api.product.request.UpdateUnitRequest;
import com.mts.backend.application.product.UnitCommandBus;
import com.mts.backend.application.product.UnitQueryBus;
import com.mts.backend.application.product.command.CreateUnitCommand;
import com.mts.backend.application.product.command.DeleteUnitByIdCommand;
import com.mts.backend.application.product.command.UpdateUnitCommand;
import com.mts.backend.application.product.query.DefaultUnitQuery;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Unit Controller", description = "Unit")
@RestController
@RequestMapping("/api/v1/units")
public class UnitController implements IController {
    
    private final UnitCommandBus unitCommandBus;
    private final UnitQueryBus unitQueryBus;
    
    public UnitController(UnitCommandBus unitCommandBus, UnitQueryBus unitQueryBus) {
        this.unitCommandBus = unitCommandBus;
        this.unitQueryBus = unitQueryBus;
    }
    
    @Operation(summary = "Tạo đơn vị tính mới")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createUnit(@Parameter(description = "Thông tin đơn vị tính", required = true) @RequestBody CreateUnitRequest createUnitRequest){
        CreateUnitCommand command =
                CreateUnitCommand.builder().name(UnitName.builder().value(createUnitRequest.getName()).build())
                        .description(createUnitRequest.getDescription())
                        .symbol(UnitSymbol.builder().value(createUnitRequest.getSymbol()).build())
                        .build();
        
        var result = unitCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Cập nhật đơn vị tính")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn vị tính")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateUnit(@Parameter(description = "ID đơn vị tính", required = true) @PathVariable("id") Integer id, @Parameter(description = "Thông tin cập nhật", required = true) @RequestBody UpdateUnitRequest updateUnitRequest){
        UpdateUnitCommand command =
                UpdateUnitCommand.builder().id(UnitOfMeasureId.of(id))
                        .name(UnitName.builder().value(updateUnitRequest.getName()).build())
                        .description(updateUnitRequest.getDescription())
                        .symbol(UnitSymbol.builder().value(updateUnitRequest.getSymbol()).build())
                        .build();
        
        var result = unitCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Lấy danh sách đơn vị tính")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllUnit(
            @Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        DefaultUnitQuery query = DefaultUnitQuery.builder()
                .page(page)
                .size(size)
                .build();
        
        var result = unitQueryBus.dispatch(query);
            
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Xóa đơn vị tính")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy đơn vị tính")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteUnit(@Parameter(description = "ID đơn vị tính", required = true) @PathVariable("id") Integer id) {
        DeleteUnitByIdCommand command = DeleteUnitByIdCommand.builder()
                .id(UnitOfMeasureId.of(id))
                .build();

        var result = unitCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
