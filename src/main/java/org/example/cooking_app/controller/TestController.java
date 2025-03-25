package org.example.cooking_app.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")

public class TestController {


    @GetMapping()
    public HttpEntity<?> getTest(){
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }
}
