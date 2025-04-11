package com.mts.backend.application.firebase;

import com.mts.backend.shared.command.CommandResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFirebaseService {
    CommandResult uploadFile(MultipartFile file);

    boolean deleteFile(String fileName);

    CommandResult listFileUrls();
}
