package com.bill.uploadManagment.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController extends  BaseController{

    @GetMapping("/admin/index")
    public String welcome() {
        return "admin-welcome";
    }
}