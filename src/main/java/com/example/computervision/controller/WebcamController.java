package com.example.computervision.controller;

import com.example.computervision.service.WebcamService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webcam")
public class WebcamController {
    private final WebcamService webcamService;

    public WebcamController(WebcamService webcamService) {
        this.webcamService = webcamService;
    }

    @GetMapping(value = "/capture", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> captureImage() {
        String imageBase64 = webcamService.captureImage();
        if (imageBase64 != null) {
            return ResponseEntity.ok("{\"image\": \"" + imageBase64 + "\"}");
        }
        return ResponseEntity.badRequest().body("{\"error\": \"Не удалось получить изображение с камеры\"}");
    }
} 