package com.bill.uploadManagment.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class FileController extends  BaseController{


    @GetMapping("/admin/upload/single")
    public String serveSingleUploadPage(){
        return "single-upload";
    }
    @GetMapping("/admin/upload/multiple")
    public String serveMultipleUploadPage(){
        return "multiple-files-upload";
    }

    @GetMapping("/admin/upload/success")
    public String serveSuccessPage(){
        return "success";
    }

    @GetMapping("/admin/upload/encrypted-file")
    public String serveUploadEncryptedFileHandler() {
        return "encrypted-upload";

    }

}
