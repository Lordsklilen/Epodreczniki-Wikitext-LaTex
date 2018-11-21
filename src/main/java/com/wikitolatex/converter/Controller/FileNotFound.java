package com.wikitolatex.converter.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFound extends RuntimeException {
    public FileNotFound(String exception) {
        super(exception);
    }
}
