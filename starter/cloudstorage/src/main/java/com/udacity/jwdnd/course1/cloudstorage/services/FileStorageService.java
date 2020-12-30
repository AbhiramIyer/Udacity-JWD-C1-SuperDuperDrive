package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mappers.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileStorageService {
    private FileMapper mapper;
    private UserService userService;

    public FileStorageService(FileMapper mapper, UserService userService) {
        this.mapper = mapper;
        this.userService = userService;
    }

    public List<File> getAllFilesByUser(String username) {
        User user = userService.getUser(username);
        return mapper.getAllFileInfoByUserId(user.getUserId());
    }

    public int uploadNewFile(File file) {
        File newFile = new File(null, file.getFileName(), file.getContentType(), file.getFileSize(), file.getUserId(), file.getFileData());
        return mapper.insertFile(newFile);
    }

    public void deleteFileById(Integer fileId) {
        mapper.deleteFileById(fileId);
    }

    public File getFileById(Integer fileId) {
        return mapper.getFileByFileId(fileId);
    }
}
