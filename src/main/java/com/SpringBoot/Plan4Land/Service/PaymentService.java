package com.SpringBoot.Plan4Land.Service;

import com.SpringBoot.Plan4Land.DTO.MembershipReqDto;
import com.SpringBoot.Plan4Land.Entity.Member;
import com.SpringBoot.Plan4Land.Entity.Membership;
import com.SpringBoot.Plan4Land.Repository.MemberRepository;
import com.SpringBoot.Plan4Land.Repository.MembershipRepository;
import com.SpringBoot.Plan4Land.Repository.PayRecordRepository;
import com.SpringBoot.Plan4Land.Repository.PaymentRepository;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
@NoArgsConstructor
public class PaymentService {
    private Key storeKey;
    private MembershipRepository membershipRepository;
    private MemberRepository memberRepository;
    private LocalDateTime now;
    private PayRecordRepository payRecordRepository;

    public PaymentService(Dotenv dotenv) {
        String secretKey = dotenv.get("PORT_ONE_SHOP");
        this.storeKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public boolean newMembership(MembershipReqDto dto) {
        try {
            Member member = memberRepository.findById(dto.getMemberId())
                    .orElseThrow(() -> new RuntimeException("멤버 조회 실패"));
            now = LocalDateTime.now();
            LocalDateTime localDateTime = now.toLocalDate().atStartOfDay();


            Membership membership = new Membership();
            membership.setMember(member);
            membership.setPayType(dto.getPayType());
            membership.setExpiryDate(localDateTime.plusDays(31).plusHours(23));
            membership.setPaymentDate(localDateTime.plusDays(31).plusHours(11));
            membership.setBillingKey(dto.getBillingKey());
            membershipRepository.save(membership);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


}
