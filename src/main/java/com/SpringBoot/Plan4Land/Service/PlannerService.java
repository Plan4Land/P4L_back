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

import java.util.List;
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
        }catch (Exception e) {
            log.error("Planner 생성 실패 : {}", e.getMessage());
            return null;
        }
    }

    public PlannerResDto findByPlannerId(Long id) {
        Planner planner = plannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 Planner가 존재하지 않습니다."));
        List<PlannerMembers> participants = plannerMembersRepository.findByPlannerId(id);
        List<PlannerMembersResDto> participantDtos = participants.stream()
                .map(member -> new PlannerMembersResDto(
                        member.getMember().getId(),
                        member.getMember().getNickname(),
                        member.getMember().getProfileImg()))
                .collect(Collectors.toList());
        Long bookmarkCount = bookMarkPlannerRepository.countByPlannerId(planner.getId());
        return PlannerResDto.fromEntity(planner, participantDtos, bookmarkCount);
    }

    public Page<PlannerResDto> getFilterdPlanner(Pageable pageable, Integer areaCode, Integer subAreaCode,
                                                 List<String> themeList, String searchQuery) {
        // 플래너 페이지 가져오기
        Page<Planner> planners = plannerRepository.getFilteredPlanners(pageable, areaCode, subAreaCode, themeList, searchQuery);

        // PlannerResDto로 변환
        return planners.map(planner -> {
            // 플래너 참여자 정보 조회
            List<PlannerMembersResDto> participants = plannerMembersRepository.findByPlannerId(planner.getId())
                    .stream()
                    .map(member -> new PlannerMembersResDto(
                            member.getMember().getId(),
                            member.getMember().getNickname(),
                            member.getMember().getProfileImg()))
                    .collect(Collectors.toList());

            Long bookmarkCount = bookMarkPlannerRepository.countByPlannerId(planner.getId());

            return PlannerResDto.fromEntity(planner, participants, bookmarkCount);
        });
    }

}
