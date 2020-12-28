package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mappers.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class CredentialsService {
    private EncryptionService encryptionService;
    private UserService userService;
    private CredentialMapper mapper;

    public CredentialsService(EncryptionService encryptionService, UserService userService, CredentialMapper mapper) {
        this.encryptionService = encryptionService;
        this.userService = userService;
        this.mapper = mapper;
    }

    public List<Credential> getAllCredentialsByUser(String username) {
        User user = userService.getUser(username);
        List<Credential> credentials = mapper.getAllCredentialsByUserId(user.getUserId());
        credentials.stream().forEach(c -> c.setClearPassword(decryptPassword(c.getEncryptedPassword(), c.getKey())));
        return credentials;
    }

    public int addNewCredential(Credential credential) {
        String key = generateKey();
        String encryptedPassword = encryptPassword(credential.getClearPassword(), key);
        Credential newCredential = new Credential(null, credential.getUrl(), credential.getUsername(), key, "", encryptedPassword, credential.getOwnerUserId());
        return mapper.insertCredential(newCredential);
    }

    private String generateKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    public void updateCredential(Credential credential) {
        // First retrieve the encryption key for the credential id being updated
        String key = getDecryptedCredentialById(credential.getCredentialId()).getKey();

        String encryptedPassword = encryptPassword(credential.getClearPassword(), key);
        credential.setEncryptedPassword(encryptedPassword);
        mapper.updateCredential(credential);
    }

    public void deleteCredentialById(Integer credentialId) {
        mapper.deleteCredential(credentialId);
    }

    public Credential getDecryptedCredentialById(Integer credentialId) {
        Credential credential = mapper.getCredentialById(credentialId);
        String decryptedPassword = decryptPassword(credential.getEncryptedPassword(), credential.getKey());
        credential.setClearPassword(decryptedPassword);
        return credential;
    }

    private String encryptPassword(String clearPassword, String key) {
        return encryptionService.encryptValue(clearPassword, key);
    }

    private String decryptPassword(String encryptedPassword, String key) {
        return encryptionService.decryptValue(encryptedPassword, key);
    }

}
