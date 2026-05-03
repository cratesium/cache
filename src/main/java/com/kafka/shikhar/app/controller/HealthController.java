package com.kafka.shikhar.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("app")
public class HealthController {

    @GetMapping("/health")
    public String getHealth(){
        return "200".toString();
    }

}
