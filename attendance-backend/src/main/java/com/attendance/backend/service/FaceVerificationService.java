package com.attendance.backend.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class FaceVerificationService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String AI_VERIFY_URL = "http://localhost:8000/verify";

    /**
     * @return Map containing:
     *  - match (boolean)
     *  - message (String)
     */
    public Map<String, Object> verifyFace(
            byte[] registeredImage,
            byte[] liveImage
    ) {

        try {
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

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            AI_VERIFY_URL,
                            requestEntity,
                            Map.class
                    );

            if (response.getBody() == null) {
                return Map.of(
                        "match", false,
                        "message", "AI verification failed"
                );
            }

            return response.getBody();

        }
        // 🔴 AI SERVER DOWN / NOT REACHABLE
        catch (ResourceAccessException ex) {
            return Map.of(
                    "match", false,
                    "message", "AI server is down. Please try again later."
            );
        }
        // 🔴 OTHER CLIENT ERRORS
        catch (RestClientException ex) {
            return Map.of(
                    "match", false,
                    "message", "AI service error"
            );
        }
        // 🔴 ANY UNEXPECTED ERROR
        catch (Exception ex) {
            return Map.of(
                    "match", false,
                    "message", "Unexpected AI verification error"
            );
        }
    }
}
