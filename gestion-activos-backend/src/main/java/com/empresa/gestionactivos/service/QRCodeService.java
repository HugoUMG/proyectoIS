package com.empresa.gestionactivos.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class QRCodeService {
    
    public byte[] generateQRCodeImage(Long activoId) {
        try {
            String qrContent = "ACTIVO:" + activoId + ":" + System.currentTimeMillis();
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 350, 350);
            
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            
            return pngOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generando QR para activo {}: {}", activoId, e.getMessage());
            throw new RuntimeException("Error generando código QR");
        }
    }
}