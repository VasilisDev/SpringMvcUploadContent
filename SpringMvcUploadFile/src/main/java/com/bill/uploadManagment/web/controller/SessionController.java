package com.bill.uploadManagment.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SessionController {


    @GetMapping("/session-expired")
    public String sessionExpired(){
        return "session_expired";
    }

    @GetMapping("/session-invalid")
    public String sessionInvalid(){
        return "session-invalid";
    }

}
