package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.Entity.BookmarkPlanner;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Planner;
import com.SpringBoot.Plan4Land.Repository.BookMarkPlannerRepository;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.PlannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.awt.print.Book;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookmarkPlannerService {
    private final MemberRepository memberRepository;
    private final PlannerRepository plannerRepository;
    private final BookMarkPlannerRepository bookmarkPlannerRepository;

    public boolean isBookmarked(String memberId, Long plannerId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Planner planner = plannerRepository.findById(plannerId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 플래너입니다."));

        return bookmarkPlannerRepository.existsByMemberAndPlanner(member, planner);
    }

    @Transactional
    public boolean putBookmarked(String memberId, Long plannerId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Planner planner = plannerRepository.findById(plannerId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 플래너입니다."));

        BookmarkPlanner bookmarkPlanner = new BookmarkPlanner();
        bookmarkPlanner.setMember(member);
        bookmarkPlanner.setPlanner(planner);

        bookmarkPlannerRepository.save(bookmarkPlanner);
        return true;
    }

    @Transactional
    public boolean deleteBookmarked(String memberId, Long plannerId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        Planner planner = plannerRepository.findById(plannerId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 플래너입니다."));

        BookmarkPlanner bookmarkPlanner = bookmarkPlannerRepository.findByMemberAndPlanner(member, planner);
        bookmarkPlannerRepository.delete(bookmarkPlanner);
        return true;
    }
}
