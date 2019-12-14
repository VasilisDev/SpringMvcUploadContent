package com.bill.uploadManagment.web.controller;

import com.bill.uploadManagment.exception.StorageException;
import com.bill.uploadManagment.exception.StorageFileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ErrorController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleInternalServerError(final Throwable throwable, final Model model) {
        logger.error("Exception during execution of Spring Security application", throwable);
        StringBuilder sb = new StringBuilder();
        sb.append("Exception during execution of Spring Security application!  ")
          .append((throwable != null && throwable.getMessage() != null ? throwable.getMessage() : "Unknown error"));

        if (throwable != null && throwable.getCause() != null) {
            sb.append("\n\nroot cause: ").append(throwable.getCause());
        }
        model.addAttribute("error", sb.toString());

        ModelAndView mav = new ModelAndView();
        mav.addObject("error", sb.toString());
        mav.setViewName("error");

        return mav;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleStorageFileNotFound(StorageFileNotFoundException exception) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("error", exception.getMessage());
        mav.setViewName("error");
        return mav;
    }

    @ExceptionHandler(StorageException.class)
    public ModelAndView handleStorageFileError(StorageException exception) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("error", exception.getMessage());
        mav.setViewName("error");
        return mav;
    }

    @ExceptionHandler({ AccessDeniedException.class })
    public ModelAndView handleAccessDeniedException(
            Exception exception) {
        ModelAndView mav = getModelAndView();
        mav.addObject("error", exception.getMessage());
        mav.setViewName("error");
        return mav;
    }
}