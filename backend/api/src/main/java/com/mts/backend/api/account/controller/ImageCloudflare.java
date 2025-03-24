package com.example.yourapp.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final S3Client s3Client;
    private final String bucketName = "chinh"; // Thay thế bằng tên bucket thật

    public ImageController(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG) // Có thể đổi thành IMAGE_PNG nếu cần
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(objectBytes.asByteArray());

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
