package com.example.computervision.service;

import com.github.sarxos.webcam.Webcam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import java.util.List;

@Slf4j
@Service
public class WebcamService {
    private Webcam webcam;
    private boolean isInitialized = false;

    @PostConstruct
    public void init() {
        try {
            log.info("Начинаем инициализацию веб-камеры...");
            
            // Получаем список всех доступных камер
            List<Webcam> webcams = Webcam.getWebcams();
            
            if (webcams.isEmpty()) {
                log.error("Веб-камеры не найдены");
                return;
            }

            // Выводим информацию о доступных камерах
            log.info("Найдено камер: {}", webcams.size());
            for (int i = 0; i < webcams.size(); i++) {
                Webcam cam = webcams.get(i);
                log.info("Камера {}: {} (разрешение: {}x{})", 
                    i, 
                    cam.getName(),
                    cam.getViewSize().getWidth(),
                    cam.getViewSize().getHeight());
            }

            // Используем первую камеру (индекс 0)
            webcam = webcams.get(0);
            log.info("Пытаемся открыть камеру: {}", webcam.getName());
            
            webcam.open();
            isInitialized = true;
            log.info("Камера успешно инициализирована: {}", webcam.getName());
            
        } catch (Exception e) {
            log.error("Не удалось инициализировать веб-камеру: {}", e.getMessage(), e);
        }
    }

    public String captureImage() {
        if (!isInitialized) {
            log.error("Попытка захвата изображения при неинициализированной камере");
            throw new RuntimeException("Веб-камера не инициализирована");
        }

        try {
            BufferedImage image = webcam.getImage();
            if (image != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                return Base64.getEncoder().encodeToString(baos.toByteArray());
            }
        } catch (Exception e) {
            log.error("Ошибка при захвате изображения: {}", e.getMessage(), e);
        }
        return null;
    }

    public byte[] captureImageBytes() {
        if (!isInitialized) {
            log.error("Попытка захвата изображения при неинициализированной камере");
            throw new RuntimeException("Веб-камера не инициализирована");
        }

        try {
            BufferedImage image = webcam.getImage();
            if (image != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            log.error("Ошибка при захвате изображения: {}", e.getMessage(), e);
        }
        return null;
    }

    @PreDestroy
    public void cleanup() {
        if (webcam != null) {
            webcam.close();
            log.info("Веб-камера освобождена");
        }
    }
} 