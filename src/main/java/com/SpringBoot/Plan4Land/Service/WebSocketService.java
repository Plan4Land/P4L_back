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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private Map<Long, WebSocketMsgDto> lastPlannerMessages;

    @PostConstruct
    private void init() {
        plannerRooms = new LinkedHashMap<>();
        lastPlannerMessages = new ConcurrentHashMap<>();
    }

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
//            log.error("방을 새로 생성함..");
            room = createRoom(webSocketMsgDto.getPlannerId());
            room.getSessions().add(session);
            log.info("Planner ID {}에 새로운 세션 생성: {}", plannerId, session.getId());
        } else {
//            log.error("있는 방에 세션만 추가함...");
//            log.error("방에 있는 세션들 : {}", room.getSessions());
            room.getSessions().add(session);
            if (webSocketMsgDto.getSender() != null) {
                webSocketMsgDto.setMessage(webSocketMsgDto.getSender() + "님이 입장했습니다.");
                sendMessageToAll(plannerId, webSocketMsgDto);
            }
            log.info("Planner ID {}에 새로운 세션 추가: {}", plannerId, session.getId());
        }
    }

    // 플래너 퇴장한 세션 제거
    public void removeSessionAndHandleExit(Long plannerId, String sender, WebSocketSession session, WebSocketMsgDto webSocketMsgDto) {
        WebSocketResDto room = plannerRooms.get(plannerId);
        if (room != null) {
            room.getSessions().remove(session);
            log.error("Planner ID {}에서 세션 제거: {}", plannerId, session.getId());
//            webSocketMsgDto.setType(WebSocketMsgDto.MessageType.CLOSE);
            webSocketMsgDto.setPlannerId(plannerId);
//            webSocketMsgDto.setSender(sender);
            Map<String, Object> data = new HashMap<>();
            data.put("plannerInfo", null);
            data.put("plans", null);
            data.put("isEditting", false);
            webSocketMsgDto.setData(data);

            sendMessageToAll(plannerId, webSocketMsgDto);

//            removePlannerMessage(plannerId);
            WebSocketMsgDto lastPlannerSender = getLastPlannerMessage(plannerId);
            if (lastPlannerSender != null
                    && lastPlannerSender.getSender().equals(webSocketMsgDto.getSender())) {
                removePlannerMessage(plannerId);
            }

            if (room.isSessionEmpty()) {
                removeRoom(plannerId); // 세션이 남아있지 않으면 방 삭제
                log.error("세션 남아있지 않아서 방 삭제");
            } else {
                log.info("Planner ID {}에서 남아있는 세션 수: {}", plannerId, room.getSessions().size());
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

    // 마지막 PLANNER 메시지 저장
    public void savePlannerMessage(Long plannerId, WebSocketMsgDto webSocketMsgDto) {
        if (webSocketMsgDto.getType() == WebSocketMsgDto.MessageType.PLANNER) {
            lastPlannerMessages.put(plannerId, webSocketMsgDto);
        }
    }
    // 마지막 PLANNER 메시지 삭제
    public void removePlannerMessage(Long plannerId) {
        lastPlannerMessages.remove(plannerId);
    }

    // 마지막 PLANNER 메시지 가져오기
    public WebSocketMsgDto getLastPlannerMessage(Long plannerId) {
        return lastPlannerMessages.get(plannerId);
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
}
