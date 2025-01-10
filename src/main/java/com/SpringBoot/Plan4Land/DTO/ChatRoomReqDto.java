package com.SpringBoot.Plan4Land.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ChatRoomReqDto {
    private String memberId; // 개설자 아이디
    private Long planningId; // 플래닝 번호
}
