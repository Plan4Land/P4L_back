package com.SpringBoot.Plan4Land.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

@Getter @Setter @Slf4j @NoArgsConstructor
public class ChatRoomResDto {
    private Long planningId; // 플래닝 번호

    @JsonIgnore // 세션 정보는 클라이언트로 전달할 필요가 없으므로 직렬화 방지
    private Set<WebSocketSession> sessions; // 채팅방에 입장한 세션 정보를 담음

    // 채팅방에 포함된 세션이 비어있는지 확인
    public boolean isSessionEmpty() {
        return this.sessions.isEmpty();
    }
}
