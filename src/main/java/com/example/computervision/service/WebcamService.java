package com.example.computervision.service;

import com.github.sarxos.webcam.Webcam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.util.List;
import java.util.Iterator;

@Slf4j
@Service
public class WebcamService {
    private final FaceDetectionService faceDetectionService;
    private Webcam webcam;
    private boolean isInitialized = false;
    private static final float JPEG_QUALITY = 0.9f; // Качество JPEG (0.0 - 1.0)

    public WebcamService(FaceDetectionService faceDetectionService) {
        this.faceDetectionService = faceDetectionService;
    }

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

            // Используем внешнюю камеру (индекс 1 - Brio 95)
            webcam = webcams.get(1);
            
            // Устанавливаем высокое разрешение
            Dimension[] nonStandardResolutions = new Dimension[] {
                new Dimension(1920, 1080), // Full HD
                new Dimension(1280, 720),  // HD
                new Dimension(800, 600),   // SVGA
                new Dimension(640, 480)    // VGA
            };

            // Пробуем установить максимально возможное разрешение
            for (Dimension resolution : nonStandardResolutions) {
                try {
                    webcam.setViewSize(resolution);
                    log.info("Установлено разрешение: {}x{}", resolution.width, resolution.height);
                    break;
                } catch (Exception e) {
                    log.warn("Не удалось установить разрешение {}x{}: {}", 
                        resolution.width, resolution.height, e.getMessage());
                }
            }

            log.info("Пытаемся открыть камеру: {}", webcam.getName());
            webcam.open();
            isInitialized = true;
            log.info("Камера успешно инициализирована: {} (разрешение: {}x{})", 
                webcam.getName(),
                webcam.getViewSize().getWidth(),
                webcam.getViewSize().getHeight());
            
        } catch (Exception e) {
            log.error("Не удалось инициализировать веб-камеру: {}", e.getMessage(), e);
        }
    }

    private byte[] compressImage(BufferedImage image) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();
        
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(JPEG_QUALITY);
        
        writer.setOutput(ios);
        writer.write(null, new IIOImage(image, null, null), param);
        writer.dispose();
        
        return baos.toByteArray();
    }

    public String captureImage() {
        if (!isInitialized) {
            log.error("Попытка захвата изображения при неинициализированной камере");
            throw new RuntimeException("Веб-камера не инициализирована");
        }

        try {
            BufferedImage image = webcam.getImage();
            if (image != null) {
                // Обрабатываем изображение для обнаружения лиц
                image = faceDetectionService.detectFaces(image);
                byte[] imageBytes = compressImage(image);
                return Base64.getEncoder().encodeToString(imageBytes);
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
                // Обрабатываем изображение для обнаружения лиц
                image = faceDetectionService.detectFaces(image);
                return compressImage(image);
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