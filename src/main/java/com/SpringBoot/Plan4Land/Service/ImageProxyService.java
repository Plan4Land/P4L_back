package com.SpringBoot.Plan4Land.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageProxyService {

    private final RestTemplate restTemplate;

    /**
     * 외부 이미지를 프록시하여 반환
     */
    public ResponseEntity<byte[]> proxyImage(String url) {
        try {
            log.info("Proxying image: {}", url);

            if (!isValidVisitKoreaUrl(url)) {
                log.warn("Invalid URL domain: {}", url);
                return ResponseEntity.badRequest().build();
            }

            byte[] imageData = downloadImage(url);
            if (imageData != null) {
                return createImageResponse(imageData);
            }

            log.warn("Failed to download image: {}", url);
            return getDefaultImage();

        } catch (Exception e) {
            log.error("Error proxying image: {}", url, e);
            return getDefaultImage();
        }
    }

    /**
     * CORS preflight 요청 처리
     */
    public ResponseEntity<Void> handleOptionsRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "*");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    // private 메서드들 (기존과 동일)
    private boolean isValidVisitKoreaUrl(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            return host != null && host.equals("tong.visitkorea.or.kr");
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] downloadImage(String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
            headers.set("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully downloaded image: {} bytes", response.getBody().length);
                return response.getBody();
            }

        } catch (Exception e) {
            log.warn("Failed to download image from: {}", url, e);
        }
        return null;
    }

    private ResponseEntity<byte[]> createImageResponse(byte[] imageData) {
        HttpHeaders headers = new HttpHeaders();

        // Content-Type 감지
        String contentType = detectContentType(imageData);
        headers.setContentType(MediaType.parseMediaType(contentType));

        // 캐시 설정 (1시간)
        headers.setCacheControl(CacheControl.maxAge(3600, TimeUnit.SECONDS).cachePublic());

        // CORS 허용
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "*");

        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }

    private String detectContentType(byte[] imageData) {
        if (imageData.length >= 2) {
            // JPEG
            if (imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8) {
                return "image/jpeg";
            }
            // PNG
            if (imageData[0] == (byte) 0x89 && imageData[1] == (byte) 0x50) {
                return "image/png";
            }
            // GIF
            if (imageData[0] == (byte) 0x47 && imageData[1] == (byte) 0x49) {
                return "image/gif";
            }
        }
        return "image/jpeg"; // 기본값
    }

    private ResponseEntity<byte[]> getDefaultImage() {
        try {
            // 기본 1x1 투명 이미지 (Base64)
            byte[] defaultImage = java.util.Base64.getDecoder().decode(
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setCacheControl(CacheControl.maxAge(86400, TimeUnit.SECONDS));

            return new ResponseEntity<>(defaultImage, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}