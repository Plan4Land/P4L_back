package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.TravelSpotReqDto;
import com.SpringBoot.Plan4Land.Repository.BookMarkSpotRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class BookMarkSpotService {

    private final BookMarkSpotRepository bookMarkSpotRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public BookMarkSpotService(BookMarkSpotRepository bookMarkSpotRepository) {
        this.bookMarkSpotRepository = bookMarkSpotRepository;
    }

    public Long getBookmarkCount(String spotId) {
        return bookMarkSpotRepository.countBySpot(spotId);
    }

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
        Long bookmarkCount = getBookmarkCount(spotId);
        TravelSpotReqDto spotDetails = getSpotDetails(Long.valueOf(spotId));
        return new SpotDetailsResponse(spotDetails, bookmarkCount);
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
