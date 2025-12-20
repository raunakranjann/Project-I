package com.attendance.backend.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class FaceVerificationService {

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verifyFace(byte[] registeredImage, byte[] liveImage) {

        String pythonUrl = "http://localhost:8000/verify";

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("registered", new ByteArrayResource(registeredImage) {
            @Override
            public String getFilename() {
                return "registered.jpg";
            }
        });

        body.add("live", new ByteArrayResource(liveImage) {
            @Override
            public String getFilename() {
                return "live.jpg";
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(pythonUrl, requestEntity, String.class);

        return response.getBody() != null && response.getBody().contains("\"match\":true");
    }
}
