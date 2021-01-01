package com.udacity.jwdnd.course1.cloudstorage.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FileUploadExceptionAdvice {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleException(Model model) {
        model.addAttribute("flashMessage", "Cannot upload files greater than 1MB. Try selecting a smaller file.");
        return "home";
    }
}
