package com.bill.uploadManagment.service;

import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface EncryptionService {
     byte[] encryption(byte[] plainText, String desKey) throws Exception;
     byte[] decrypt(String keys, byte[] plainText) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException;
     void storeEncryptedFile(MultipartFile file,String cipherText) throws Exception;
     Path decryptedContent(String filename, String key) throws Exception;
     Path storeDecryptedFile(byte[] decContent, String filename) throws Exception ;



    }
