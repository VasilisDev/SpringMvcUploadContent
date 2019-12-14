package com.bill.uploadManagment.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class IOUtils {


    public static byte[] File2byte(String filePath) throws IOException {


        byte[] buffer= null ;
        try {
            buffer = Files.readAllBytes(Paths.get(filePath));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static Path WriteBytes2File(byte[] buf, String filePath) {

        Path path = null;

        try {
             path = Paths.get(filePath);
            Files.write(path, buf);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static Path Byte2File(byte[] content,String filename){
        Path path = null;
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(filename));
            path= Files.write(Paths.get(filename), encoded);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }



    public static Optional<String> getFileExtension(String filename){
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static Optional<String> removeSignedFromFileExtension(String filename) {
     return Optional.ofNullable(filename)
                .filter(f -> f.contains(".signed"))
                .map(f -> f.substring(0,filename.length() - 7));
       }

}
