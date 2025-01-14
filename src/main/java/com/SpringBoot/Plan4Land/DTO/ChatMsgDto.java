package com.SpringBoot.Plan4Land.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
public class ChatMsgDto {
    public enum MessageType {
        ENTER, TALK, CLOSE
    }

    private MessageType type; // 방 진입, 메시지
    private Long plannerId; // 플래너 번호
    private String sender; // 보내는 사람
    private String message; // 메시지 내용

    @Builder
    public ChatMsgDto(Long plannerId, String sender, String message) {
        this.plannerId = plannerId;
        this.sender = sender;
        this.message = message;
    }
}
