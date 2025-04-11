package com.mts.backend.application.firebase.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ImageFirebaseResponse {
    private String fileName;
    private String fileUrl;
    private String fileType;
    private long fileSize;
    private String downloadUrl;
    private String storagePath;
}
