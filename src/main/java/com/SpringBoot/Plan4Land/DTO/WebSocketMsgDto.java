package com.SpringBoot.Plan4Land.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class WebSocketMsgDto {
    public enum MessageType {
        CHAT, PLANNER, ENTER, CLOSE
    }

    private MessageType type; // 메시지 타입
    private Long plannerId;   // 플래너 ID
    private String sender;    // 보낸 사람 (채팅 시 사용)
    private String message;   // 채팅 내용 (채팅 시 사용)
    private Map<String, Object> data; // 플래너 업데이트 정보 (플래너 업데이트 시 사용)

    @Builder
    public WebSocketMsgDto(MessageType type, Long plannerId, String sender, String message, Map<String, Object> data) {
        this.type = type;
        this.plannerId = plannerId;
        this.sender = sender;
        this.message = message;
        this.data = data;
    }
}
