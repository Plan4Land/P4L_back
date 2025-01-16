package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.ChatMsgDto;
import com.SpringBoot.Plan4Land.DTO.ChatRoomResDto;
import com.SpringBoot.Plan4Land.Entity.ChatMsg;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Repository.ChatMsgRepository;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.PlannerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {
    private final ObjectMapper objectMapper;
    private final PlannerRepository plannerRepository;
    private final MemberRepository memberRepository;
    private final ChatMsgRepository chatMsgRepository;
    private Map<Long, ChatRoomResDto> chatRooms;

    @PostConstruct // 의존성 주입 이후 초기화를 수행하는 메서드
    private void init() {
        chatRooms = new LinkedHashMap<>(); // 채팅방 정보를 담을 맵 (순서 보장을 위해 LinkedHashMap 사용)
    }

    public List<ChatMsgDto> getChatMsgById(Long plannerId) {
        List<ChatMsg> chatMsgs = chatMsgRepository.findTop10ByPlannerIdOrderBySendTimeDesc(plannerId);
        return chatMsgs.stream()
                .map(chatMsg -> ChatMsgDto.builder()
                        .plannerId(chatMsg.getPlanner().getId())
                        .sender(chatMsg.getSender().getNickname()) // Assuming Member has a `getName()` method
                        .message(chatMsg.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    public ChatRoomResDto findRoomByPlannerId(Long plannerId) {
        return chatRooms.get(plannerId);
    }
    // 방 개설하기
    public ChatRoomResDto createRoom(Long plannerId) {
//        String randomId = UUID.randomUUID().toString();
//        log.info("UUID : {}", randomId);
        ChatRoomResDto chatRoom = ChatRoomResDto.builder()
//                .roomId(randomId)
                .plannerId(plannerId)
                .build();
        chatRooms.put(plannerId, chatRoom); // 방 생성, 키와 방 정보 추가
        return chatRoom;
    }
    // 방 삭제
    public void removeRoom(Long plannerId) {
        ChatRoomResDto room = chatRooms.get(plannerId);
        if (room != null) {
            if (room.isSessionEmpty()) {
                chatRooms.remove(plannerId);
            }
        }
    }

    // 채팅방에 입장한 세션 추가
    public void addSessionAndHandleEnter(Long plannerId,
                                         WebSocketSession session,
                                         ChatMsgDto chatMessage) {
        ChatRoomResDto room = findRoomByPlannerId(plannerId);
//        ChatRoomResDto room = chatRooms.get(plannerId);
        if (room == null) {
            // 방이 없으면 생성
            room = createRoom(chatMessage.getPlannerId());
        }
        if(room != null) {
            room.getSessions().add(session); // 채팅방에 입장한 세션을 추가
//            if(chatMessage.getSender() != null) { // 채팅방에 입장한 사용자가 있으면
//                chatMessage.setMessage(chatMessage.getSender() + "님이 입장했습니다.");
//                // 채팅방에 입장 메시지 전송 코드 추가
//                sendMessageToAll(plannerId, chatMessage);
//            }
            log.info("Planner ID {}에 새로운 세션 추가: {}", plannerId, session.getId());
        }
    }

    // 채팅방에서 퇴장한 세션 제거
    public void removeSessionAndHandleExit(Long plannerId,
                                           WebSocketSession session,
                                           ChatMsgDto chatMessage) {
        ChatRoomResDto room = chatRooms.get(plannerId);
        if (room != null) {
            room.getSessions().remove(session); // 채팅방에서 퇴장한 세션 제거
            if (chatMessage.getSender() != null) { // 채팅방에서 퇴장한 사용자가 있으면
                chatMessage.setMessage(chatMessage.getSender() + "님이 퇴장하였습니다.");
                // 채팅방에 퇴장 메시지 전송 코드 추가
                sendMessageToAll(chatMessage.getPlannerId(), chatMessage);
            }
            log.error("Planner ID {}에서 세션 제거: {}", plannerId, session.getId());
            if (room.isSessionEmpty()) {
                removeRoom(plannerId); // 세션이 남아있지 않으면 방 삭제
                log.error("세션 남아있지 않아서 방 삭제");
            }
        }
    }

    public void saveMessageToDB(ChatMsgDto chatMessage) {
        if ((chatMessage.getType() != ChatMsgDto.MessageType.ENTER) && (chatMessage.getType() != ChatMsgDto.MessageType.CLOSE)) {
            log.info(chatMessage.toString());

            Planner planner = plannerRepository.findById(chatMessage.getPlannerId())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 플래너"));
            Member sender = memberRepository.findByNickname(chatMessage.getSender())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 회원"));

            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setContent(chatMessage.getMessage());
            chatMsg.setSender(sender);
            chatMsg.setPlanner(planner); // plannerId로 Planner 객체 찾아서 저장

            // 전송시간 자동 설정
            chatMsg.setSendTime(LocalDateTime.now());

            // DB에 메시지 저장
            chatMsgRepository.save(chatMsg);  // 실제 DB 저장
        }
    }

    public void sendMessageToAll(Long plannerId, ChatMsgDto message) {
        saveMessageToDB(message); // 메시지 저장
//        ChatRoomResDto room = findRoomById(roomId);
        ChatRoomResDto room = chatRooms.get(plannerId);
        if (room != null) {
            for (WebSocketSession session : room.getSessions()) {
                // 해당 세션에 메시지 발송
                sendMessage(session, message);
            }
        }
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error("메시지 전송 실패 - 세션 ID: {} - 에러: {}", session.getId(), e.getMessage());
        }
    }
}
