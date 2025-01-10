package com.SpringBoot.Plan4Land.DTO;

import com.SpringBoot.Plan4Land.Entity.BookmarkSpot;
import com.SpringBoot.Plan4Land.Entity.Member;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BookmarkSpotReqDto {
    private boolean isBookmarked;
    private long bookmarkCount;

    public BookmarkSpot toEntity(Member member, String spotId) {
        return BookmarkSpot.builder()
                .member(member)
                .spot(spotId)
                .build();
    }
}
