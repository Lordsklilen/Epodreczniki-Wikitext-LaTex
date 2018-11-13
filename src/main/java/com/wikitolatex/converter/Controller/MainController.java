package com.wikitolatex.converter.Controller;

import com.wikitolatex.converter.Model.POJOClass;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/")
    POJOClass index()
    {
        return new POJOClass(10,"jazda");
    }
}
