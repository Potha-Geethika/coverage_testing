// Unresolved import (framework/JDK): org.springframework.web.bind.annotation.RestController
// Unresolved import (framework/JDK): org.springframework.web.bind.annotation.RequestMapping
// ===== Current file: src/main/java/com/carbo/job/controllers/HelloController.java =====
package com.carbo.job.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {
    
    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }
    
}

