package com.bill.uploadManagment.web.controller;

import com.bill.uploadManagment.service.EncryptionService;
import com.bill.uploadManagment.service.impl.StorageServiceImpl;
import com.bill.uploadManagment.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FileUploadController  extends  BaseController{

    private final StorageServiceImpl storageService;
    private final EncryptionService encryptionService;


    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    public FileUploadController(StorageServiceImpl storageService, EncryptionService encryptionService) {
        this.storageService = storageService;
        this.encryptionService = encryptionService;
    }


    @PostMapping("/admin/upload/single")
    public String SingleFileUploadHandler(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes){
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/admin/upload/success";
    }

    @PostMapping("/admin/upload/multiple")
    public String MultipleFilesUploadHandler(@RequestParam("files") MultipartFile[] files,
                                             RedirectAttributes redirectAttributes) {
        Arrays.stream(files)
                .forEach(storageService::store);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded all files!");

        return "redirect:/admin/upload/success";
    }


    @GetMapping(value = "/files")
    public String ServeUploadedFilesHandler(Model model) {
        List<String> urls = storageService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(FileUploadController.class,
                                        "serveFile",path.getFileName().toString())
                                .build().encode().toString())
                .collect(Collectors.toList());


        List<String> names = storageService
                .loadAll()
                .map(path ->
                        path.getFileName().toString())
                .collect(Collectors.toList());

        List<Map<String, String>> plainFilesAndNames = new ArrayList<>();
        plainFilesAndNames.add(MapUtils.zipToMap(urls,names));


        List<Map<String, String>> listOfPlainFiles = plainFilesAndNames.stream().map(
                entry->  entry.entrySet().stream().filter(file-> !file.getValue().endsWith(".signed"))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                                 )
                                .collect(Collectors.toList());

        List<Map<String, String>> listOfEncryptedFiles = plainFilesAndNames.stream().map(
                entry->  entry.entrySet().stream().filter(file-> file.getValue().endsWith(".signed"))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        )
                .collect(Collectors.toList());

        model.addAttribute("files", listOfPlainFiles);
        model.addAttribute("encryptedFiles", listOfEncryptedFiles);


        return "files";

    }

    @GetMapping(value = "/files/download-all")
    public String downloadAllAsZipHandler() throws IOException {

        this.storageService.exportAll();
        Resource file = storageService.loadAsResource("download.zip"); // get your input stream here
        String zipUri = MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "serveFile",file.getFilename())
                .build().encode().toString();

        ModelAndView mav = getModelAndView();
        mav.addObject("zipUri",zipUri);
        mav.setViewName("download-zip");

        return "redirect:/files/"+ file.getFilename();
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename)
            throws Exception {
        logger.info("serveFile:" + filename);
        if(filename.endsWith(".signed")){
            filename = this.encryptionService.decryptedContent(filename,"12345678").toString();
        }


        Resource file = storageService.loadAsResource(filename);
        String contentType = this.getContentType(file);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @PostMapping("/admin/upload/encrypted-file")
    public String uploadEncryptedFileHandler(@RequestParam("file")MultipartFile multipartFile,
                                             RedirectAttributes redirectAttributes) {

        try{
            this.encryptionService.storeEncryptedFile(multipartFile, "12345678");

        }catch (Exception e){

            return "encrypted-upload";
        }
        redirectAttributes.addFlashAttribute("message", "You successfully upload an encrypted file!");
        return "redirect:/admin/upload/success";

    }


    private String getContentType(Resource file){
        String contentType = null;

        try {
            contentType = getRequest().getServletContext().getMimeType(file.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        return  contentType;
    }
}
