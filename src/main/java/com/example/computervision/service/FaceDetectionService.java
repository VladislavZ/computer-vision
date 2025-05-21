package com.example.computervision.service;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_objdetect.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.file.Paths;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_objdetect.*;

@Slf4j
@Service
public class FaceDetectionService {
    private CascadeClassifier faceDetector;
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private static final int FACE_RECT_THICKNESS = 2;

    @PostConstruct
    public void init() {
        try {
            // Путь к файлу каскадного классификатора
            String cascadePath = Paths.get("src", "main", "resources", "haarcascades", 
                "haarcascade_frontalface_default.xml").toString();
            
            File cascadeFile = new File(cascadePath);
            if (!cascadeFile.exists()) {
                throw new RuntimeException("Файл каскадного классификатора не найден: " + cascadePath);
            }

            faceDetector = new CascadeClassifier(cascadePath);
            if (faceDetector.empty()) {
                throw new RuntimeException("Не удалось загрузить каскадный классификатор");
            }

            log.info("FaceDetectionService успешно инициализирован");
        } catch (Exception e) {
            log.error("Ошибка при инициализации FaceDetectionService: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось инициализировать FaceDetectionService", e);
        }
    }

    public BufferedImage detectFaces(BufferedImage image) {
        try {
            // Конвертируем BufferedImage в Mat
            Mat mat = bufferedImageToMat(image);
            
            // Конвертируем в оттенки серого
            Mat grayMat = new Mat();
            cvtColor(mat, grayMat, COLOR_BGR2GRAY);
            
            // Выполняем обнаружение лиц
            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(grayMat, faces);
            
            // Рисуем прямоугольники вокруг обнаруженных лиц
            for (long i = 0; i < faces.size(); i++) {
                Rect rect = faces.get(i);
                rectangle(mat, rect, FACE_RECT_COLOR, FACE_RECT_THICKNESS, LINE_8, 0);
            }
            
            // Конвертируем обратно в BufferedImage
            return matToBufferedImage(mat);
        } catch (Exception e) {
            log.error("Ошибка при обнаружении лиц: {}", e.getMessage(), e);
            return image;
        }
    }

    private Mat bufferedImageToMat(BufferedImage image) {
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CV_8UC3);
        mat.data().put(pixels);
        return mat;
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        byte[] data = new byte[mat.rows() * mat.cols() * (int)(mat.elemSize())];
        mat.data().get(data);
        
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        
        return image;
    }
} 