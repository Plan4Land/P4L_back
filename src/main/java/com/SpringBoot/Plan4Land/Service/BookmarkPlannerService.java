package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.PlannerResDto;
import com.SpringBoot.Plan4Land.Entity.BookmarkPlanner;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Repository.BookMarkPlannerRepository;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.Planner.PlannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkPlannerService {
    
    private final MemberRepository memberRepository;
    private final PlannerRepository plannerRepository;
    private final BookMarkPlannerRepository bookmarkPlannerRepository;

    private static final String MEMBER_NOT_FOUND_MESSAGE = "존재하지 않는 회원입니다.";
    private static final String PLANNER_NOT_FOUND_MESSAGE = "존재하지 않는 플래너입니다.";
    private static final String BOOKMARK_NOT_FOUND_MESSAGE = "북마크가 존재하지 않습니다.";

    /**
     * 북마크 상태 확인
     */
    public boolean isBookmarked(String memberId, Long plannerId) {
        log.debug("북마크 상태 확인 - memberId: {}, plannerId: {}", memberId, plannerId);
        
        Member member = findMemberById(memberId);
        Planner planner = findPlannerById(plannerId);

        return bookmarkPlannerRepository.existsByMemberAndPlanner(member, planner);
    }

    /**
     * 북마크 추가
     */
    @Transactional
    public boolean addBookmark(String memberId, Long plannerId) {
        log.debug("북마크 추가 시작 - memberId: {}, plannerId: {}", memberId, plannerId);
        
        Member member = findMemberById(memberId);
        Planner planner = findPlannerById(plannerId);

        // 이미 북마크가 존재하는지 확인
        if (bookmarkPlannerRepository.existsByMemberAndPlanner(member, planner)) {
            log.warn("이미 북마크가 존재합니다 - memberId: {}, plannerId: {}", memberId, plannerId);
            return false;
        }

        BookmarkPlanner bookmarkPlanner = BookmarkPlanner.builder()
                .member(member)
                .planner(planner)
                .build();

        bookmarkPlannerRepository.save(bookmarkPlanner);
        log.info("북마크 추가 완료 - memberId: {}, plannerId: {}", memberId, plannerId);
        return true;
    }

    /**
     * 북마크 삭제
     */
    @Transactional
    public boolean removeBookmark(String memberId, Long plannerId) {
        log.debug("북마크 삭제 시작 - memberId: {}, plannerId: {}", memberId, plannerId);
        
        Member member = findMemberById(memberId);
        Planner planner = findPlannerById(plannerId);

        Optional<BookmarkPlanner> bookmarkOpt =
                Optional.ofNullable(bookmarkPlannerRepository.findByMemberAndPlanner(member, planner));
        
        if (bookmarkOpt.isEmpty()) {
            log.warn("삭제할 북마크가 존재하지 않습니다 - memberId: {}, plannerId: {}", memberId, plannerId);
            return false;
        }

        bookmarkPlannerRepository.delete(bookmarkOpt.get());
        log.info("북마크 삭제 완료 - memberId: {}, plannerId: {}", memberId, plannerId);
        return true;
    }

    /**
     * 사용자의 북마크한 플래너 목록 조회 (페이징)
     */
    @Transactional
    public Page<PlannerResDto> getBookmarkedPlanners(String memberId, int page, int size) {
        log.debug("북마크 플래너 목록 조회 - memberId: {}, page: {}, size: {}", memberId, page, size);
        
        Member member = findMemberById(memberId);
        
        // 페이지 유효성 검증
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("잘못된 페이지 정보입니다.");
        }

        List<Long> plannerIds = bookmarkPlannerRepository.findBookmarkPlannersByMember(member);
        
        if (plannerIds.isEmpty()) {
            log.info("북마크한 플래너가 없습니다 - memberId: {}", memberId);
            return Page.empty();
        }

        List<Planner> planners = plannerRepository.findByIdInAndIsPublicTrue(plannerIds);

        List<PlannerResDto> plannerResDtos = planners.stream()
                .map(planner -> PlannerResDto.fromEntity(planner, null, null))
                .toList();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        // 실제 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), plannerResDtos.size());
        
        List<PlannerResDto> pageContent = start <= end ? 
                plannerResDtos.subList(start, end) : List.of();

        return new PageImpl<>(pageContent, pageable, plannerResDtos.size());
    }

    /**
     * 회원 조회 (private helper method)
     */
    private Member findMemberById(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("회원을 찾을 수 없습니다 - memberId: {}", memberId);
                    return new IllegalArgumentException(MEMBER_NOT_FOUND_MESSAGE);
                });
    }

    /**
     * 플래너 조회 (private helper method)
     */
    private Planner findPlannerById(Long plannerId) {
        return plannerRepository.findById(plannerId)
                .orElseThrow(() -> {
                    log.error("플래너를 찾을 수 없습니다 - plannerId: {}", plannerId);
                    return new IllegalArgumentException(PLANNER_NOT_FOUND_MESSAGE);
                });
    }

    // 기존 메서드들과의 호환성을 위한 deprecated 메서드들
    @Deprecated(since = "1.0", forRemoval = true)
    public boolean putBookmarked(String memberId, Long plannerId) {
        log.warn("putBookmarked 메서드는 deprecated입니다. addBookmark를 사용하세요.");
        return addBookmark(memberId, plannerId);
    }

    @Deprecated(since = "1.0", forRemoval = true)
    public boolean deleteBookmarked(String memberId, Long plannerId) {
        log.warn("deleteBookmarked 메서드는 deprecated입니다. removeBookmark를 사용하세요.");
        return removeBookmark(memberId, plannerId);
    }
}