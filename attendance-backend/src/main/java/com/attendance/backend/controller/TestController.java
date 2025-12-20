package com.attendance.backend.controller;

import com.attendance.backend.service.FaceVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/test")
public class TestController {

    private final FaceVerificationService faceService;

    @Autowired
    public TestController(FaceVerificationService faceService) {
        this.faceService = faceService;
    }

    @PostMapping(
            value = "/face",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String testFace(
            @RequestPart("registered") MultipartFile registered,
            @RequestPart("live") MultipartFile live
    ) throws Exception {

        boolean result = faceService.verifyFace(
                registered.getBytes(),
                live.getBytes()
        );

        return result ? "MATCH" : "NO MATCH";
    }
}
