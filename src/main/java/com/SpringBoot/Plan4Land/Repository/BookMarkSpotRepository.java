package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.BookmarkSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookMarkSpotRepository extends JpaRepository<BookmarkSpot, Long> {
    Long countBySpot(String spot);
}
