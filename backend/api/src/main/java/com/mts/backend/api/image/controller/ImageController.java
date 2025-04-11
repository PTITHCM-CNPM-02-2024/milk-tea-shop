package com.mts.backend.api.image.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.application.firebase.IFirebaseService;
import com.mts.backend.infrastructure.firebase.FirebaseService;
import com.mts.backend.shared.command.CommandResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController implements IController {
    
    private final FirebaseService firebaseService;
    
    public ImageController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(MultipartFile file) {
        var response = firebaseService.uploadFile(file);
        
        return response.isSuccess() ? ResponseEntity.ok(response.getData()) : handleError(response);
    }
    
    @PostMapping("/delete")
    public ResponseEntity<?> deleteImage(String fileName) {
        var response = firebaseService.deleteFile(fileName);
        
        return response ? ResponseEntity.ok().build() : handleError(CommandResult.businessFail("Failed to delete image"));
    }
    
    @PostMapping("/list")
    public ResponseEntity<?> listImageUrls() {
        var response = firebaseService.listFileUrls();
        
        return response.isSuccess() ? ResponseEntity.ok(response.getData()) : handleError(response);
    }
}
