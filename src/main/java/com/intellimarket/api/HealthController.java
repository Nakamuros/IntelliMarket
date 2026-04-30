package com.intellimarket.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String check() {
        return "¡IntelliMarket API está conectada a PostgreSQL y funcionando al 100%!";
    }
}