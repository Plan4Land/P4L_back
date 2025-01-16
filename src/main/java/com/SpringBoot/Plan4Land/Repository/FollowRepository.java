package com.SpringBoot.Plan4Land.Repository;

import com.SpringBoot.Plan4Land.Entity.Follow;
import com.SpringBoot.Plan4Land.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findByFollowerAndFollowed(Member follower, Member followed);

    Follow findByFollowerIdAndFollowedId(String followerId, String followedId);
}
