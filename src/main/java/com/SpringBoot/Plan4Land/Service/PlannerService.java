package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.PlannerMembersResDto;
import com.SpringBoot.Plan4Land.DTO.PlannerReqDto;
import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Entity.PlannerMembers;
import com.SpringBoot.Plan4Land.Repository.BookMarkPlannerRepository;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.PlannerMembersRepository;
import com.SpringBoot.Plan4Land.Repository.PlannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlannerService {
    private final PlannerRepository plannerRepository;
    private final MemberRepository memberRepository;
    private final PlannerMembersRepository plannerMembersRepository;
    private final BookMarkPlannerRepository bookMarkPlannerRepository;

    @Transactional
    public Long makePlanner(PlannerReqDto plannerReqDto) {
        try {
            Member member = memberRepository.findById(plannerReqDto.getId())
                    .orElseThrow(() -> new RuntimeException("Planner 작성 중 회원 조회 실패"));

            Planner planner = plannerReqDto.toEntity(member);
            Planner savedPlanner = plannerRepository.save(planner);

            return savedPlanner.getId();
        } catch (RuntimeException e) {
            log.error("존재하지 않는 회원입니다.");
            return null;
        } catch (Exception e) {
            log.error("Planner 생성 실패 : {}", e.getMessage());
            return null;
        }
    }

    public PlannerResDto findByPlannerId(Long id) {
        Planner planner = plannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 Planner가 존재하지 않습니다."));
        List<PlannerMembers> participants = plannerMembersRepository.findByPlannerId(id);
        List<PlannerMembersResDto> participantDtos = participants.stream()
                .map(member -> {
                    // PlannerMembers의 상태를 함께 조회
                    String state = member.getState() != null ? member.getState().name() : null;
                    return new PlannerMembersResDto(
                            member.getMember().getId(),
                            member.getMember().getNickname(),
                            member.getMember().getProfileImg(),
                            state // 상태를 포함
                    );
                })
                .collect(Collectors.toList());
        Long bookmarkCount = bookMarkPlannerRepository.countByPlannerId(planner.getId());
        return PlannerResDto.fromEntity(planner, participantDtos, bookmarkCount);
    }

    public Page<PlannerResDto> getFilterdPlanner(Pageable pageable, String areaCode, String subAreaCode,
                                                 String themeList, String searchQuery) {

        String[] arr = themeList == null ? null : themeList.split(",");

        String theme1 = arr != null && arr.length > 0 ? arr[0] : null;
        String theme2 = arr != null && arr.length > 1 ? arr[1] : null;
        String theme3 = arr != null && arr.length > 2 ? arr[2] : null;

        if(arr != null){
            log.warn(Arrays.toString(arr));
        }

        Page<Planner> planners = plannerRepository.getFilteredPlanners(pageable, areaCode, subAreaCode, searchQuery, theme1, theme2, theme3);

        // PlannerResDto로 변환
        return planners.map(planner -> {
            List<PlannerMembersResDto> participants = plannerMembersRepository.findByPlannerId(planner.getId())
                    .stream()
                    .map(member -> {
                        // PlannerMembers의 상태를 함께 조회
                        String state = member.getState() != null ? member.getState().name() : null;
                        return new PlannerMembersResDto(
                                member.getMember().getId(),
                                member.getMember().getNickname(),
                                member.getMember().getProfileImg(),
                                state // 상태를 포함
                        );
                    })
                    .collect(Collectors.toList());

            Long bookmarkCount = bookMarkPlannerRepository.countByPlannerId(planner.getId());

            return PlannerResDto.fromEntity(planner, participants, bookmarkCount);
        });
    }

    public List<PlannerResDto> getTop3BookmarkedPlanners() {
        List<Long> topPlannerIds = bookMarkPlannerRepository.findTop3PlannerIdsByBookmarkCount();
        List<Planner> topPlanners = plannerRepository.findAllById(topPlannerIds);
        return topPlanners.stream()
                .map(planner -> {
                    Long bookmarkCount = bookMarkPlannerRepository.countByPlannerId(planner.getId());
                    return PlannerResDto.fromEntity(planner, null, bookmarkCount);
                })
                .collect(Collectors.toList());
    }

    public Page<Planner> getPlannersByOwner(String memberId, Pageable pageable) {
        return plannerRepository.findByOwnerId(memberId, pageable);
    }

    public boolean inviteMember(String memberId, Long plannerId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 멤버를 찾을 수 없습니다."));
        Planner planner = plannerRepository.findById(plannerId)
                .orElseThrow(() -> new RuntimeException("해당 플래너를 찾을 수 없습니다."));

        PlannerMembers plannerMembers = new PlannerMembers();
        plannerMembers.setMember(member);
        plannerMembers.setPlanner(planner);
        plannerMembersRepository.save(plannerMembers);

        return true;
    }
}
