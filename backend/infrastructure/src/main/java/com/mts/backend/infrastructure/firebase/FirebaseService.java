package com.mts.backend.infrastructure.firebase;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import com.mts.backend.application.firebase.response.ImageFirebaseResponse;
import com.mts.backend.shared.command.CommandResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class FirebaseService implements com.mts.backend.application.firebase.IFirebaseService {

    private final static String PUBLIC_URL = "https://firebase.googleapis.com/v0/b/";

    private final FirebaseApp firebaseApp;

    public FirebaseService(FirebaseProperties firebaseApp) {
        this.firebaseApp = firebaseApp.getFirebaseApp();
    }

    @Override
    public CommandResult uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        var client = StorageClient.getInstance(firebaseApp);

        var bucket = client.bucket().getName();

        var storage = StorageClient.getInstance().bucket().getStorage();
        BlobId blobId = BlobId.of(bucket, fileName);
        List<Acl> acl = Collections.singletonList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .setAcl(acl)
                .build();
        

        Blob result = null;
        try {
            result = storage.create(blobInfo, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Không thể upload file", e);
        }
        var response = ImageFirebaseResponse.builder()
                .fileName(result.getName())
                .fileUrl(result.getMediaLink())
                .fileType(result.getContentType())
                .fileSize(result.getSize())
                .downloadUrl(result.getMediaLink())
                .storagePath(result.getBucket())
                .build();

        return CommandResult.success(response);

    }

    @Override
    public boolean deleteFile(String fileName) {
        var client = StorageClient.getInstance();

        var bucket = client.bucket().getName();

        var storage = StorageClient.getInstance().bucket().getStorage();
        BlobId blobId = BlobId.of(bucket, fileName);


        return storage.delete(blobId);
    }

    @Override
    public CommandResult listFileUrls() {
        var client = StorageClient.getInstance();

        var bucket = client.bucket().getName();

        var storage = StorageClient.getInstance().bucket().getStorage();

        Iterable<Blob> blobs = storage.list(bucket).iterateAll();

        var fileUrls = new ArrayList<String>();

        for (var blob : blobs) {
            String fileUrl = PUBLIC_URL + bucket + "/" + blob.getName();
            fileUrls.add(fileUrl);
        }

        return CommandResult.success(fileUrls);

    }

}
