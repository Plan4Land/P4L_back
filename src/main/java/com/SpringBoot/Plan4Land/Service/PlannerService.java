package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.Constant.Role;
import com.SpringBoot.Plan4Land.Constant.State;
import com.SpringBoot.Plan4Land.DTO.PlannerMembersResDto;
import com.SpringBoot.Plan4Land.DTO.PlannerReqDto;
import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Plan;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Entity.PlannerMembers;
import com.SpringBoot.Plan4Land.Repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
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
    private final PlanRepository planRepository;
    private final ChatMsgRepository chatMsgRepository;

    @Transactional
    // 플래너 생성
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

    @Transactional
    // 플래너 수정
    public PlannerResDto editPlannerInfo(PlannerReqDto plannerReqDto, Long plannerId) {
        try {
            Member member = memberRepository.findById(plannerReqDto.getId())
                    .orElseThrow(() -> new RuntimeException("Planner 수정 중 회원 조회 실패"));
            // 플래너 조회
            Planner planner = plannerRepository.findById(plannerId)
                    .orElseThrow(() -> new RuntimeException("Planner 수정 중 플래너 조회 실패"));

            planner.setTitle(plannerReqDto.getTitle());
            planner.setTheme(plannerReqDto.getTheme());
            planner.setStartDate(plannerReqDto.getStartDate());
            planner.setEndDate(plannerReqDto.getEndDate());
            planner.setArea(plannerReqDto.getArea());
            planner.setSubArea(plannerReqDto.getSubArea());
            planner.setThumbnail(plannerReqDto.getThumbnail());

            // 변경 내용 저장
            plannerRepository.save(planner);
            return findByPlannerId(plannerId);
        } catch (RuntimeException e) {
            log.error("플래너 수정 중 오류 발생: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("플래너 수정 실패: {}", e.getMessage());
            return null;
        }
    }

    // 플래너 공개여부 수정
    @Transactional
    public PlannerResDto updateIsPublic(Long plannerId, boolean isPublic) {
        try {
            Planner planner = plannerRepository.findById(plannerId)
                    .orElseThrow(() -> new RuntimeException("해당하는 플래너가 존재하지 않습니다."));

            planner.setPublic(isPublic);
            plannerRepository.save(planner);
            return findByPlannerId(plannerId);
        } catch (Exception e) {
            log.error("플래너 공개여부 수정 중 에러 : {}", e);
            throw e;
        }
    }

    // 플래너 상세조회
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

    // 플래너 삭제
    @Transactional
    public boolean removePlannerInfo(Long plannerId, String userId) {
        try {
            Member member = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));
            Planner planner = plannerRepository.findById(plannerId).orElseThrow(() -> new RuntimeException("해당 플래너를 찾을 수 없습니다."));

            if (planner.getOwner().getId().equals(member.getId()) || member.getRole() == Role.ROLE_ADMIN) {
                plannerMembersRepository.deleteByPlannerId(plannerId);
                bookMarkPlannerRepository.deleteByPlannerId(plannerId);
                chatMsgRepository.deleteByPlannerId(plannerId);
                planRepository.deleteByPlannerId(plannerId);
                plannerRepository.deleteById(plannerId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    // 플래너 탈퇴
    @Transactional
    public boolean leavePlanner(Long plannerId, String userId) {
        try {
            Member member = memberRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));
            Planner planner = plannerRepository.findById(plannerId)
                    .orElseThrow(() -> new RuntimeException("해당 플래너를 찾을 수 없습니다."));

            plannerMembersRepository.deleteByMemberIdAndPlannerId(userId, plannerId);
            return true;
        } catch (Exception e) {
            log.error("플래너 멤버 탈퇴 중 에러 : {}", e.getMessage());
            return false;
        }
    }

    // Plan 조회
    public List<Plan> getPlans(Long plannerId) {
        return planRepository.findByPlannerId(plannerId);
    }

    // Plan 삭제 및 삽입
    @Transactional
    public List<Plan> deleteAndInsertPlans(Long plannerId, List<Plan> newPlans) {
        planRepository.deleteByPlannerId(plannerId);

        Planner planner = plannerRepository.findById(plannerId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 Planner ID"));

        for (Plan newPlan : newPlans) {
            newPlan.setPlanner(planner);
            planRepository.save(newPlan);
        }
        return planRepository.findByPlannerId(plannerId);
    }

    // 플래너 검색 및 조회하기
    public Page<PlannerResDto> getFilteredPlanner(int currentPage, int pageSize, String areaCode, String subAreaCode,
                                                  String themeList, String searchQuery, String sortBy) {

        String[] themes = themeList == null ? new String[0] : themeList.split(",");

        List<Planner> planners = plannerRepository.getFilteredPlanners(areaCode, subAreaCode, searchQuery,
                themes.length > 0 ? themes[0].trim() : null,
                themes.length > 1 ? themes[1].trim() : null,
                themes.length > 2 ? themes[2].trim() : null);

        List<PlannerResDto> plannerResDtos = planners.stream()
                .map(planner -> {
                    Long bookmarkCount = bookMarkPlannerRepository.countByPlannerId(planner.getId());
                    return PlannerResDto.fromEntity(planner, null, bookmarkCount);
                })
                .toList();

        // bookmarkCount와 id 기준 정렬
        Comparator<PlannerResDto> comparator = switch (sortBy) {
            case "LatestAsc" -> Comparator.comparing(PlannerResDto::getId);
            case "BookmarkAsc" -> Comparator.comparing(PlannerResDto::getBookmarkCount);
            case "BookmarkDesc" -> Comparator.comparing(PlannerResDto::getBookmarkCount).reversed();
            default -> Comparator.comparing(PlannerResDto::getId).reversed();
        };

        List<PlannerResDto> sortedPlannerResDtos = plannerResDtos.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        // 정렬된 리스트에서 페이징 처리
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        int start = Math.min((int) pageable.getOffset(), sortedPlannerResDtos.size());
        int end = Math.min((start + pageable.getPageSize()), sortedPlannerResDtos.size());
        List<PlannerResDto> paginatedList = sortedPlannerResDtos.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, sortedPlannerResDtos.size());
    }

    // 북마크 상위 3개 플래너 가져오기
    public List<PlannerResDto> getTopBookmarkedPlanners() {
        Pageable pageable = PageRequest.of(0, 5);

        List<Planner> topPlanners = bookMarkPlannerRepository.findTopPlanners(pageable);

        return topPlanners.stream()
                .map(planner -> {
                    Long bookmarkCount = bookMarkPlannerRepository.countByPlannerId(planner.getId());
                    return PlannerResDto.fromEntity(planner, null, bookmarkCount);
                })
                .collect(Collectors.toList());
    }

    // 내 소유 플래너 목록 가져오기 (최신순으로 정렬)
    public Page<Planner> getPlannersByOwner(String memberId, Pageable pageable) {
        // 최신순 정렬 추가
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("regDate")));
        return plannerRepository.findByOwnerId(memberId, sortedPageable);
    }

    // 특정 유저의 공개 플래너 목록 가져오기 (최신순으로 정렬)
    public Page<Planner> getPrivatePlannersByOwner(String memberId, Pageable pageable) {
        // 최신순 정렬 추가
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.desc("regDate")));
        return plannerRepository.findByOwnerIdAndIsPublicTrue(memberId, sortedPageable);
    }

    // 플래닝에 멤버 초대
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

    // 초대받은 플래닝 조회
    public List<PlannerResDto> selectInvitedPlanners(String memberId) {
        List<PlannerMembers> invites = plannerMembersRepository.findByMemberIdAndState(memberId, State.WAIT);
        return invites.stream()
                .map(plannerMember -> {
                    Planner planner = plannerMember.getPlanner();

                    List<PlannerMembersResDto> participantDtos = plannerMembersRepository.findByPlannerId(planner.getId())
                            .stream()
                            .map(member -> {
                                String state = member.getState() != null ? member.getState().name() : null;
                                return new PlannerMembersResDto(
                                        member.getMember().getId(),
                                        member.getMember().getNickname(),
                                        member.getMember().getProfileImg(),
                                        state // 상태 포함
                                );
                            })
                            .collect(Collectors.toList());

                    // 4. PlannerResDto로 변환 후 반환
                    return PlannerResDto.fromEntity(planner, participantDtos, 0L);
                })
                .collect(Collectors.toList());
    }

    // 초대 수락
    public boolean acceptInvitation(String memberId, Long plannerId) {
        PlannerMembers plannerMember = plannerMembersRepository
                .findByMemberIdAndPlannerId(memberId, plannerId)
                .orElseThrow(() -> new IllegalArgumentException("초대 정보가 존재하지 않습니다."));
        plannerMember.setState(State.ACCEPT);
        plannerMembersRepository.save(plannerMember);

        return true;
    }

    // 초대 거절
    public boolean rejectInvitation(String memberId, Long plannerId) {
        PlannerMembers plannerMember = plannerMembersRepository
                .findByMemberIdAndPlannerId(memberId, plannerId)
                .orElseThrow(() -> new IllegalArgumentException("초대 정보가 존재하지 않습니다."));
        plannerMembersRepository.delete(plannerMember);

        return true;
    }

    // 내가 작성한, 포함된 플래너 목록 가져오기
    public Page<PlannerResDto> getPlanners(String memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Member owner = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("User not found"));

        // Repository에서 플래너 조회
        Page<Planner> planners = plannerRepository.findPlannersByOwnerOrMember(owner, pageable);

        // Dto로 변환
        List<PlannerResDto> plannerDtos = planners.getContent().stream()
                .map(planner -> PlannerResDto.fromEntity(planner, null, 0L)) // bookmarkCount는 0으로 설정
                .collect(Collectors.toList());

        // 페이지네이션 처리된 결과 반환
        return new PageImpl<>(plannerDtos, pageable, planners.getTotalElements());
    }

    public Long getLastPlannerId() {
        return plannerRepository.findLastId() + 1;
    }

}
