package com.udacity.jwdnd.course1.cloudstorage.mappers;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CredentialMapper {
    @Select("SELECT * FROM CREDENTIALS WHERE userid=#{userId}")
    @Results(value = {
            @Result(property = "encryptedPassword", column = "password")
    })
    List<Credential> getAllCredentialsByUserId(int userId);

    @Insert("INSERT INTO CREDENTIALS (url, username, key, password, userid) VALUES(#{url}, #{username}, #{key}, #{encryptedPassword}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "credentialId")
    int insertCredential(Credential credential);

    @Update("UPDATE CREDENTIALS SET url=#{url}, username=#{username}, password=#{encryptedPassword} WHERE credentialId=#{credentialId}")
    void updateCredential(Credential credential);

    @Delete("DELETE FROM CREDENTIALS WHERE credentialId=#{credentialId}")
    void deleteCredential(int credentialId);

    @Select("SELECT * FROM CREDENTIALS WHERE credentialId=#{credentialId}")
    @Results(value = {
            @Result(property = "encryptedPassword", column = "password")
    })
    Credential getCredentialById(int credentialId);
}

