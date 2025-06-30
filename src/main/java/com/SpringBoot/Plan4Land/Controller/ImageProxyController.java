package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.Service.ImageProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image-proxy")
@Slf4j
public class ImageProxyController {

    private final ImageProxyService imageProxyService;

    // 이미지 프록시 엔드포인트
    @GetMapping
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) {
        return imageProxyService.proxyImage(url);
    }


    //CORS preflight 요청 처리
    @CrossOrigin
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        return imageProxyService.handleOptionsRequest();
    }

}