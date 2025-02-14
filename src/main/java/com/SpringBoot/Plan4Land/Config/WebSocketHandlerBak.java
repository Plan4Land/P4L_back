package com.SpringBoot.Plan4Land.Config;

import com.SpringBoot.Plan4Land.DTO.ChatMsgDto;
import com.SpringBoot.Plan4Land.Service.ChatService;
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
public class WebSocketHandlerBak extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    // 세션과 채팅방을 매핑하는 데 사용 (사용자가 어떤 채팅방에 속해 있는지 등록)
    private final Map<WebSocketSession, Long> sessionPlannerIdMap = new ConcurrentHashMap<>();

    @Override // handleTextMessage 메서드는 TextWebSocketHandler 클래스에서 제공하는 메서드로, WebSocket을 통해 수신된 텍스트 메시지를 처리하는 데 사용
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload(); // 클라이언트가 전송한 메시지
        // JSON 문자열을 ChatMessageDto로 변환 작업
        ChatMsgDto chatMessage = objectMapper.readValue(payload, ChatMsgDto.class);
        Long plannerId = chatMessage.getPlannerId();

        if (chatMessage.getType() == ChatMsgDto.MessageType.ENTER) {
            // 사용자가 채팅방에 입장할 때
            sessionPlannerIdMap.put(session, chatMessage.getPlannerId());
            // ChatService를 통해 방에 세션 추가
            chatService.addSessionAndHandleEnter(plannerId, session, chatMessage);
        } else if (chatMessage.getType() == ChatMsgDto.MessageType.CLOSE) {
            chatService.removeSessionAndHandleExit(plannerId, session, chatMessage);
        } else {
            chatService.sendMessageToAll(plannerId, chatMessage);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
            throws Exception{
        Long plannerId = sessionPlannerIdMap.remove(session);
        if(plannerId != null) {
            ChatMsgDto chatMessage = new ChatMsgDto();
            chatMessage.setType(ChatMsgDto.MessageType.CLOSE);
            chatService.removeSessionAndHandleExit(plannerId, session, chatMessage);
        }
    }
}
