package com.udacity.jwdnd.course1.cloudstorage.mappers;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {
    @Select("SELECT fileid, filename FROM FILES WHERE userid=#{userId}")
    List<File> getAllFileInfoByUserId(int userId);

    @Select("SELECT * from FILES WHERE fileid=#{fileId}")
    File getFileByFileId(int fileId);

    @Select("SELECT filename from FILES WHERE filename=#{fileName}")
    File getFileByFileName(String fileName);

    @Insert("INSERT INTO FILES (filename, contenttype, filesize, userid, filedata) VALUES(#{fileName}, #{contentType}, #{fileSize}, #{userId}, #{fileData})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int insertFile(File file);

    @Delete("DELETE FROM FILES WHERE fileid=#{fileId}")
    void deleteFileById(int fileId);
}

