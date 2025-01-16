package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.DTO.MemberResDto;
import com.SpringBoot.Plan4Land.Entity.Follow;
import com.SpringBoot.Plan4Land.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findByFollowerAndFollowed(Member follower, Member followed);

    Follow findByFollowerIdAndFollowedId(String followerId, String followedId);

    @Query("SELECT f.followed.uid FROM Follow f WHERE f.follower.uid = :followed")
    List<Long> getFollowedIdBy(Long followed);

    @Query("SELECT f.follower.uid FROM Follow f WHERE f.followed.uid = :follower")
    List<Long> getFollowerIdBy(Long follower);
}
