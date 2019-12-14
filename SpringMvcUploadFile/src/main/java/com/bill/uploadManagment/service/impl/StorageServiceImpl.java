package com.bill.uploadManagment.service.impl;

import com.bill.uploadManagment.configuration.FileStorageProperties;
import com.bill.uploadManagment.exception.StorageException;
import com.bill.uploadManagment.exception.StorageFileNotFoundException;
import com.bill.uploadManagment.exception.ZipAllFilesException;
import com.bill.uploadManagment.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class StorageServiceImpl implements StorageService {


    private final Path rootPath;
    private static final String FILE_SEPARATOR = File.separator;
    private final FileStorageProperties fileStorageProperties;


    @Autowired
    public StorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
        this.rootPath = this.getPath();
//        this.createDirectory();

    }


    @Override
    public void store(MultipartFile file) {


        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {

            if (file.isEmpty()) {
                throw new StorageException("Cannot store empty file " + filename);
            }
            if (filename.contains("..")) {
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootPath.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Could not store file " + file.getOriginalFilename(),e);
        }
    }

    @Override
    public void deleteFile(String filename) {
        try {
            Files.deleteIfExists(this.load(filename));
        } catch (IOException e) {
            throw new StorageFileNotFoundException("File with name "+filename+" not found!",e);
        }
    }


    @Override
    public Path getPath(){

        return Paths.get(String.join(FILE_SEPARATOR, fileStorageProperties.getUploadDir()))
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public void exportAll() throws  IOException,ZipAllFilesException{

        Path zipFilePath = this.getPath().resolve("download.zip");


        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Path sourceDirPath = this.rootPath;

            Files.walk(sourceDirPath).filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());

                        try {
                            zipOutputStream.putNextEntry(zipEntry);
                            zipOutputStream.write(Files.readAllBytes(path));
                            zipOutputStream.closeEntry();
                        } catch (Exception e) {
                            throw new ZipAllFilesException("Something went wrong while zipping all files!Try later!",e);
                        }
                    });
        }
    }

    @Override
    public void createDirectory(Path directory) throws IOException {
        try {
            Files.createDirectories(directory);
        } catch (DirectoryNotEmptyException ex) {
            throw new StorageException("Could not create the directory!",ex);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootPath, 1)
                    .filter(path -> !Files.isDirectory(path))
                    .map(this.rootPath::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootPath.resolve(filename).normalize();
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path filePath = load(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("File not found " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new StorageFileNotFoundException("File not found " + filename, ex);
        }
    }
}
