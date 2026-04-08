package com.empresa.gestionactivos.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping
    public String testGet() {
        return "Backend funcionando correctamente";
    }

    @PostMapping
    public String testPost(@RequestBody String data) {
        return "Recibido: " + data;
    }
}