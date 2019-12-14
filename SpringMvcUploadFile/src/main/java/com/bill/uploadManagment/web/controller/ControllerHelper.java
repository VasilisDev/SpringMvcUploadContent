package com.bill.uploadManagment.web.controller;

import java.util.function.Function;

class ControllerHelper {
    static Function<String, String> redirect = (path)-> "redirect:" + path;
}
