package com.bill.uploadManagment.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService  {


    void store(MultipartFile file);

    Path getPath();

    void deleteFile(String filename);

    void exportAll() throws IOException;

    void createDirectory(Path directory) throws IOException;

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    }