package com.SpringBoot.Plan4Land.Entity;

import antlr.collections.impl.BitSet;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Table(name="Bookmark_Planner")
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkPlanner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bk_pl_id")
    private Long id;

    // 북마크한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    // 북마크 당한 플래너
    @ManyToOne
    @JoinColumn(name = "planner_id")
    @JsonIgnore
    private Planner planner;


}
