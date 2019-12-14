package com.bill.uploadManagment.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import static com.bill.uploadManagment.web.controller.ControllerHelper.redirect;


@Controller
public class DefaultController extends  BaseController{
    @GetMapping(value={"/default","/"})
    public String defaultAfterLogin() {
        if (getRequest().isUserInRole("ROLE_ADMIN")) {
            return redirect.apply("/admin/index");
        }
        return redirect.apply("/files");
    }}