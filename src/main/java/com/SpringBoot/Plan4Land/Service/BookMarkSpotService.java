package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.TravelSpotReqDto;
import com.SpringBoot.Plan4Land.Entity.BookmarkSpot;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Repository.BookMarkSpotRepository;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookMarkSpotService {
    private final BookMarkSpotRepository bookMarkSpotRepository;
    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // spot 상세 정보 가져오기
    public TravelSpotReqDto getSpotDetails(Long spotId) {
        String query = "SELECT new com.SpringBoot.Plan4Land.DTO.TravelSpotReqDto(" +
                "t.id, t.title, t.tel, t.thumbnail, t.areaCode, t.sigunguCode, " +
                "t.addr1, t.addr2, t.cat1, t.cat2, t.cat3, t.typeId, " +
                "t.createdTime, t.modifiedTime, t.mapX, t.mapY) " +
                "FROM TravelSpot t WHERE t.id = :spotId";

        return entityManager.createQuery(query, TravelSpotReqDto.class)
                .setParameter("spotId", spotId)
                .getSingleResult();
    }

    // 북마크 수와 spot 상세 정보를 함께 반환
    public SpotDetailsResponse getSpotDetailsWithBookmark(String spotId) {
        Long bookmarkCount = bookMarkSpotRepository.countBySpot(spotId);
        TravelSpotReqDto spotDetails = getSpotDetails(Long.valueOf(spotId));
        return new SpotDetailsResponse(spotDetails, bookmarkCount);
    }

    // 북마크 추가
    public String addBookmark(Long memberId, String spotId) {
        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isEmpty()) {
            throw new IllegalArgumentException("해당 회원이 존재하지 않습니다.");
        }

        // 이미 북마크가 있는지 확인
        if (bookMarkSpotRepository.existsByMemberAndSpot(member.get(), spotId)) {
            return "이미 북마크한 장소입니다.";
        }

        // 북마크 추가
        BookmarkSpot newBookmark = new BookmarkSpot();
        newBookmark.setMember(member.get());
        newBookmark.setSpot(spotId);

        bookMarkSpotRepository.save(newBookmark);
        return "북마크가 추가되었습니다.";
    }

    // 북마크 삭제
    public String removeBookmark(Long memberId, String spotId) {
        Optional<Member> member = memberRepository.findById(memberId);

        if (member.isEmpty()) {
            throw new IllegalArgumentException("해당 회원이 존재하지 않습니다.");
        }

        // 북마크 존재 여부 확인
        Optional<BookmarkSpot> bookmarkSpot = bookMarkSpotRepository.findByMemberAndSpot(member.get(), spotId);
        if (bookmarkSpot.isEmpty()) {
            return "북마크가 존재하지 않습니다.";
        }

        // 북마크 삭제
        bookMarkSpotRepository.delete(bookmarkSpot.get());
        return "북마크가 삭제되었습니다.";
    }

    // 응답용 DTO 클래스
    public static class SpotDetailsResponse {
        private final TravelSpotReqDto spotDetails;
        private final Long bookmarkCount;

        public SpotDetailsResponse(TravelSpotReqDto spotDetails, Long bookmarkCount) {
            this.spotDetails = spotDetails;
            this.bookmarkCount = bookmarkCount;
        }

        public TravelSpotReqDto getSpotDetails() {
            return spotDetails;
        }

        public Long getBookmarkCount() {
            return bookmarkCount;
        }
    }
}
