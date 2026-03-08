package com.climb.api.controller;

import com.climb.api.model.dto.ApiResponse;
import com.climb.api.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
@RequiredArgsConstructor
public class HelloController {

    private final HelloService helloService;

    @GetMapping
    public ApiResponse<String> hello() {
        return ApiResponse.ok(helloService.greet());
    }
}
