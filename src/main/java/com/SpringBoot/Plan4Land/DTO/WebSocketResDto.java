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

@Getter
@Setter
@Slf4j
@NoArgsConstructor
public class WebSocketResDto {
    private Long plannerId;

    @JsonIgnore
    private Set<WebSocketSession> sessions; // 플래너 입장한 세션 정보 담음

    // 플래너에 포함된 세션이 비어있는지 확인
    public boolean isSessionEmpty() {return this.sessions.isEmpty();}

    @Builder
    public WebSocketResDto(Long plannerId) {
        this.plannerId = plannerId;
        // 동시성 문제를 해결하기 위해서 사용
        this.sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }
}
