package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.DTO.ChatMsgDto;
import com.SpringBoot.Plan4Land.DTO.ChatRoomResDto;
import com.SpringBoot.Plan4Land.Service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

//    @GetMapping("/room/{plannerId}")
//    public ChatRoomResDto findRoomById(@PathVariable Long plannerId) {
//        return chatService.findMsgById(plannerId);
//    }

    @GetMapping("/msg/{plannerId}")
    public List<ChatMsgDto> getChatMsgsByPlannerId(@PathVariable Long plannerId) {
        return chatService.getChatMsgById(plannerId);
    }
}
