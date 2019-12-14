package com.bill.uploadManagment.service.impl;

import com.bill.uploadManagment.service.EncryptionService;
import com.bill.uploadManagment.service.StorageService;
import com.bill.uploadManagment.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;


@Service
public class EncryptionServiceImpl implements EncryptionService {



    private final StorageService storageService;

    public EncryptionServiceImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public byte[] encryption(byte[] plainText, String desKey) throws Exception {

        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(new DESKeySpec(desKey.getBytes()));
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, securekey);
            return cipher.doFinal(plainText);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Override
    public byte[] decrypt(String keys, byte[] plainText)
            throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
                   NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {

        SecureRandom random = new SecureRandom();
        DESKeySpec desKey = new DESKeySpec(keys.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(desKey);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        return cipher.update(plainText);

    }



    @Override
    public void storeEncryptedFile(MultipartFile file, String cipherText) throws Exception {
        Path rootPath = this.storageService.getPath();
        byte [] encContent = this.encryption(file.getBytes(),cipherText);
        IOUtils.WriteBytes2File(encContent,
                rootPath + File.separator + file.getOriginalFilename() + ".signed");
    }


    @Override
    public Path storeDecryptedFile(byte[] decContent, String filename) throws Exception {


        Path rootPath = this.storageService.getPath();
        String newFilename = IOUtils.removeSignedFromFileExtension(filename).orElseThrow(FileNotFoundException::new);
        Path path = IOUtils.WriteBytes2File(decContent,rootPath +File.separator + newFilename);
        this.storageService.deleteFile(filename);
        return this.storageService.load(path.toString());

    }

    @Override
    public Path decryptedContent(String filename, String key)
                throws Exception {
        String encryptedFilenamePath = this.storageService.load(filename).toString();
        byte[] encryptedFile2bytes = IOUtils.File2byte(encryptedFilenamePath);
        byte[] decryptedFile =  this.decrypt(key,encryptedFile2bytes);
        return this.storeDecryptedFile(decryptedFile,filename);
    }
}
