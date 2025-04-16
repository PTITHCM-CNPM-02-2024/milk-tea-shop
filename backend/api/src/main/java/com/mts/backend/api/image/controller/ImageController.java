package com.mts.backend.api.image.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.application.firebase.IFirebaseService;
import com.mts.backend.infrastructure.firebase.FirebaseService;
import com.mts.backend.shared.command.CommandResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Image Controller", description = "Image")
@RestController
@RequestMapping("/api/v1/images")
public class ImageController implements IController {
    
    private final FirebaseService firebaseService;
    
    public ImageController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }
    
    @Operation(summary = "Tải lên ảnh")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi upload ảnh")
    })
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@Parameter(description = "File ảnh", required = true) @RequestParam("file") MultipartFile file)  {
        var response = firebaseService.uploadFile(file);
        
        return response.isSuccess() ? ResponseEntity.ok(response.getData()) : handleError(response);
    }
    
    @Operation(summary = "Xóa ảnh")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi xóa ảnh")
    })
    @PostMapping("/delete")
    public ResponseEntity<?> deleteImage(@Parameter(description = "Tên file ảnh", required = true) String fileName) {
        var response = firebaseService.deleteFile(fileName);
        
        return response ? ResponseEntity.ok().build() : handleError(CommandResult.businessFail("Failed to delete image"));
    }
    
    @Operation(summary = "Lấy danh sách URL ảnh")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @PostMapping("/list")
    public ResponseEntity<?> listImageUrls() {
        var response = firebaseService.listFileUrls();
        
        return response.isSuccess() ? ResponseEntity.ok(response.getData()) : handleError(response);
    }
}
