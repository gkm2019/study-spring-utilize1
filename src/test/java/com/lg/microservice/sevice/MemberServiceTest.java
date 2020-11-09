package com.lg.microservice.sevice;

import com.lg.microservice.domain.Member;
import com.lg.microservice.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest //이거 없으면 autowired실패함
@Transactional //rollback해야지~
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("회원가입")
    public void join() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        //em.flush(); //쿼리문 insert까지 보고싶을때~ 트랜잭션 어노테이션이 롤백 해준다 알아서
        //함수 위에 @Rollback(false)하면 실제로 넣어진다.
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    @DisplayName("중복 회원 예외")
    public void memberDuple() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("kim1");

        Member member2 = new Member();
        member2.setName("kim1");

        //when
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));

        //then
        //fail("예외가 발생해야 한다.");
    }
}