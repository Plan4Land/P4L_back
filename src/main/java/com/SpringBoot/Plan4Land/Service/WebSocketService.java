package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.ChatMsgDto;
import com.SpringBoot.Plan4Land.DTO.WebSocketMsgDto;
import com.SpringBoot.Plan4Land.DTO.WebSocketResDto;
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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WebSocketService {
    private final ObjectMapper objectMapper;
    private final PlannerRepository plannerRepository;
    private final MemberRepository memberRepository;
    private final ChatMsgRepository chatMsgRepository;
    private Map<Long, WebSocketResDto> plannerRooms;

    @PostConstruct
    private void init() {plannerRooms = new LinkedHashMap<>();}

    // 최근 10개의 채팅 대화내용 가져오기
    public List<WebSocketMsgDto> getWebSocketMsgById(Long plannerId) {
        List<ChatMsg> chatMsgs = chatMsgRepository.findTop10ByPlannerIdOrderBySendTimeDesc(plannerId);
        return chatMsgs.stream()
                .map(chatMsg -> WebSocketMsgDto.builder()
                        .plannerId(chatMsg.getPlanner().getId())
                        .sender(chatMsg.getSender().getNickname()) // Assuming Member has a `getName()` method
                        .message(chatMsg.getContent())
                        .data(null)  // 채팅일 땐 data = null
                        .build())
                .collect(Collectors.toList());
    }

    public WebSocketResDto findRoomByPlannerId(Long plannerId) {
        return plannerRooms.get(plannerId);
    }

    // 방 개설
    public WebSocketResDto createRoom(Long plannerId) {
        WebSocketResDto plannerRoom = WebSocketResDto.builder()
                .plannerId(plannerId)
                .build();
        plannerRooms.put(plannerId, plannerRoom);
        return plannerRoom;
    }

    // 방 삭제
    public void removeRoom(Long plannerId) {
        WebSocketResDto room = plannerRooms.get(plannerId);
        if (room != null) {
            if (room.isSessionEmpty()) {
                plannerRooms.remove(plannerId);
            }
        }
    }

    // 플래너 접속한 세션 추가
    public void addSessionAndHandleEnter(Long plannerId, WebSocketSession session, WebSocketMsgDto webSocketMsgDto) {
        WebSocketResDto room = findRoomByPlannerId(plannerId);
        // 방이 없으면 생성
        if (room == null) {
            room = createRoom(webSocketMsgDto.getPlannerId());
        }

        room.getSessions().add(session);
        log.info("Planner ID {}에 새로운 세션 추가: {}", plannerId, session.getId());
    }

    // 플래너 퇴장한 세션 제거
    public void removeSessionAndHandleExit(Long plannerId, WebSocketSession session, WebSocketMsgDto webSocketMsgDto) {
        WebSocketResDto room = plannerRooms.get(plannerId);
        if (room != null) {
            room.getSessions().remove(session);
            log.error("Planner ID {}에서 세션 제거: {}", plannerId, session.getId());
            if (room.isSessionEmpty()) {
                removeRoom(plannerId); // 세션이 남아있지 않으면 방 삭제
                log.error("세션 남아있지 않아서 방 삭제");
            }
        }
    }

    // 채팅 메시지 DB에 저장
    public void saveMessageToDB(WebSocketMsgDto webSocketMsgDto) {
        if ((webSocketMsgDto.getType()) == WebSocketMsgDto.MessageType.CHAT) {
            Planner planner = plannerRepository.findById(webSocketMsgDto.getPlannerId())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 플래너"));
            Member sender = memberRepository.findByNickname(webSocketMsgDto.getSender())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 회원"));

            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setContent(webSocketMsgDto.getMessage());
            chatMsg.setSender(sender);
            chatMsg.setPlanner(planner);
            chatMsg.setSendTime(LocalDateTime.now());

            // DB에 메시지 저장
            chatMsgRepository.save(chatMsg);
        }
    }

    public void sendMessageToAll(Long plannerId, WebSocketMsgDto webSocketMsgDto) {
        saveMessageToDB(webSocketMsgDto);

        WebSocketResDto room = plannerRooms.get(plannerId);
        if (room != null) {
            for (WebSocketSession session : room.getSessions()) {
                sendMessage(session, webSocketMsgDto);
            }
        }
    }

    public <T> void sendMessage(WebSocketSession session, T webSocketMsgDto) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(webSocketMsgDto)));
        } catch (IOException e) {
            log.error("메시지 전송 실패 - 세션 ID: {} - 에러: {}", session.getId(), e.getMessage());
        }
    }

    public void broadcastPlanner(Long plannerId, Map<String, Object> updatedData) {
        WebSocketMsgDto updateMessage = WebSocketMsgDto.builder()
                .type(WebSocketMsgDto.MessageType.PLANNER) // 메시지 타입: PLANNER
                .plannerId(plannerId)
                .data(updatedData) // 업데이트된 데이터를 포함
                .build();

        WebSocketResDto room = plannerRooms.get(plannerId);
        if (room != null) {
            for (WebSocketSession session : room.getSessions()) {
                sendMessage(session, updateMessage);
            }
        } else {
            log.error("플래너 ID {}에 해당하는 방이 없습니다. 브로드캐스트 중단.", plannerId);
        }
    }
}
