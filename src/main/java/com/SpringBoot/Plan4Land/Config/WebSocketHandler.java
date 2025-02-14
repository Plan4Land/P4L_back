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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final WebSocketService webSocketService;

    //    private final Map<WebSocketSession, Long> sessionPlannerIdMap = new ConcurrentHashMap<>();
    // WebSocketSession과 sender를 매핑
    private final Map<WebSocketSession, String> sessionSenderMap = new ConcurrentHashMap<>();

    // WebSocketSession과 plannerId를 매핑
    private final Map<WebSocketSession, Long> sessionPlannerIdMap = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        WebSocketMsgDto webSocketMsgDto = objectMapper.readValue(payload, WebSocketMsgDto.class);
        Long plannerId = webSocketMsgDto.getPlannerId();
        String sender = webSocketMsgDto.getSender();

        switch (webSocketMsgDto.getType()) {
            case ENTER:
                sessionPlannerIdMap.put(session, plannerId);
                sessionSenderMap.put(session, sender);
                webSocketService.addSessionAndHandleEnter(plannerId, session, webSocketMsgDto);
//                log.warn("{} plannerId: {} sender: {}", sessionPlannerIdMap.size(), plannerId, sender);
//                log.warn(String.valueOf(sessionSenderMap.size()));

                WebSocketMsgDto lastPlannerMessage = webSocketService.getLastPlannerMessage(plannerId);
                if (lastPlannerMessage != null) {
                    try {
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(lastPlannerMessage)));
                    } catch (IOException e) {
                        log.error("메시지 전송 실패 - 세션 ID: {} - 에러: {}", session.getId(), e.getMessage());
                    }
                }
                break;
            case CHAT:
                webSocketService.sendMessageToAll(plannerId, webSocketMsgDto);
                break;
            case PLANNER:
                webSocketService.savePlannerMessage(plannerId, webSocketMsgDto);
                webSocketService.sendMessageToAll(plannerId, webSocketMsgDto);
                break;
            default:
                log.error("Unknown message type: {}", webSocketMsgDto.getType());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        Long plannerId = sessionPlannerIdMap.remove(session);
        String sender = sessionSenderMap.remove(session);
        if(plannerId != null && sender != null) {
            WebSocketMsgDto webSocketMsgDto = new WebSocketMsgDto();
            webSocketMsgDto.setType(WebSocketMsgDto.MessageType.CLOSE);
            webSocketMsgDto.setSender(sender);
            webSocketService.removeSessionAndHandleExit(plannerId, sender, session, webSocketMsgDto, sessionSenderMap, sessionPlannerIdMap);
        }
    }
}
