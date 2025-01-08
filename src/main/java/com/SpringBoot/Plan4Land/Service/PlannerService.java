package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.PlannerReqDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.PlannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlannerService {
    /**
     * 0. 매개변수로 새로 등록할 플래너의 제목 등을 dto(제목, memberId(PK) or memberEmail(Unique하다면)를 통해 끌어옵니다.
     * 1. Member 찾아야함. HOW? memberRepository.findByEmail({이메일}); => Optional<Member> 같은게 반환됨
     * 2. Planner newPlanner = new Planner();
     * 3. newPlanner.bulider().~~~~~~.제목({제목}).owner({위에서 조회한 member 인스턴스}).~~~~~~~build();
     * 4. plannerRepository.save(newPlanner);
     *
     * */
    private final PlannerRepository plannerRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean makePlanner(PlannerReqDto plannerReqDto) {
        try {
            Member member = memberRepository.findById(plannerReqDto.getId())
                    .orElseThrow(() -> new RuntimeException("Planner 작성 중 회원 조회 실패"));

            Planner planner = new Planner();
            planner.setTitle(plannerReqDto.getTitle());
            planner.setTheme(plannerReqDto.getTheme());
            planner.setStartDate(plannerReqDto.getStartDate());
            planner.setEndDate(plannerReqDto.getEndDate());
            planner.setThumbnail(plannerReqDto.getThumbnail());
            planner.setPublic(plannerReqDto.isPublic());
            planner.setOwner(member);
            plannerRepository.save(planner);
            return true;
        } catch (RuntimeException e) {
            return false;
        }catch (Exception e) {
            log.error("Planner 생성 실패 : {}", e.getMessage());
            return false;
        }
    }
}
