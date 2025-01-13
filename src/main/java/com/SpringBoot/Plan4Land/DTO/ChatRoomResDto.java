package com.SpringBoot.Plan4Land.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter @Slf4j @NoArgsConstructor
public class ChatRoomResDto {
//    private String roomId; // 채팅방 ID
    private Long plannerId; // 플래닝 번호

    @JsonIgnore // 세션 정보는 클라이언트로 전달할 필요가 없으므로 직렬화 방지
    private Set<WebSocketSession> sessions; // 채팅방에 입장한 세션 정보를 담음

    // 채팅방에 포함된 세션이 비어있는지 확인
    public boolean isSessionEmpty() {
        return this.sessions.isEmpty();
    }

    @Builder
    public ChatRoomResDto(String roomId, Long plannerId) {
//        this.roomId = roomId;
        this.plannerId = plannerId;
        // 동시성 문제를 해결하기 위해서 사용
        this.sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }
}
