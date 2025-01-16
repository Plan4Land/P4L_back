package com.SpringBoot.Plan4Land.Config;

import com.SpringBoot.Plan4Land.DTO.ChatMsgDto;
import com.SpringBoot.Plan4Land.DTO.WebSocketMsgDto;
import com.SpringBoot.Plan4Land.Service.WebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final WebSocketService webSocketService;

    private final Map<WebSocketSession, Long> sessionPlannerIdMap = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload : {}", payload);
        WebSocketMsgDto webSocketMsgDto = objectMapper.readValue(payload, WebSocketMsgDto.class);
        Long plannerId = webSocketMsgDto.getPlannerId();

        switch (webSocketMsgDto.getType()) {
            case ENTER:
                sessionPlannerIdMap.put(session, webSocketMsgDto.getPlannerId());
                webSocketService.addSessionAndHandleEnter(plannerId, session, webSocketMsgDto);
                break;
            case CLOSE:
                webSocketService.removeSessionAndHandleExit(plannerId, session, webSocketMsgDto);
                break;
            case CHAT:
                webSocketService.sendMessageToAll(plannerId, webSocketMsgDto);
                break;
            case PLANNER:
                webSocketService.broadcastPlanner(plannerId, webSocketMsgDto.getData());
                break;
            default:
                log.error("Unknown message type: {}", webSocketMsgDto.getType());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        log.info("연결 해제 이후 동작 : {}", session);
        Long plannerId = sessionPlannerIdMap.remove(session);
        if(plannerId != null) {
            WebSocketMsgDto webSocketMsgDto = new WebSocketMsgDto();
            webSocketMsgDto.setType(WebSocketMsgDto.MessageType.CLOSE);
            webSocketService.removeSessionAndHandleExit(plannerId, session, webSocketMsgDto);
        }
    }
}
