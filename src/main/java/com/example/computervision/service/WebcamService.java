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
                log.info("Камера {}: {}", i, cam.getName());
            }

            // Выбираем внешнюю камеру (обычно она вторая в списке)
            // Если это не так, можно изменить индекс
            int selectedCameraIndex = 1; // Индекс внешней камеры
            if (selectedCameraIndex < webcams.size()) {
                webcam = webcams.get(selectedCameraIndex);
                webcam.open();
                isInitialized = true;
                log.info("Выбрана камера: {}", webcam.getName());
            } else {
                log.error("Внешняя камера не найдена");
            }
        } catch (Exception e) {
            log.error("Не удалось инициализировать веб-камеру: {}", e.getMessage());
        }
    }

    public String captureImage() {
        if (!isInitialized) {
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
            log.error("Ошибка при захвате изображения: {}", e.getMessage());
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