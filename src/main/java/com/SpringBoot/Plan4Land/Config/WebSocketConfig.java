package com.SpringBoot.Plan4Land.Config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.warn("WebSocket 핸들러가 등록되었습니다.");
        registry.addHandler(webSocketHandler, "ws/planner").setAllowedOrigins("*");
    }
}
