package com.lg.microservice.sevice;

import com.lg.microservice.domain.Member;
import com.lg.microservice.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //** jpa는 트랜잭션 안에서 수행된다.spring이 제공하는 어노테이션을 import한다.
public class MemberService {
    @Autowired
    MemberRepository memberRepository; //member Repository를 사용할 것이기 때문에 주입한다.
    /*
    * 생성자 injection
    private final MemberRepository memberRepository; //변경 불가하게 final넣기
    public MemberService(MemberRepository memberRepository){
    * this.memberRepository = memberRepository;
    }
    *
    * ver 2 (가장 나을 수도있음..ㅎ) 사용
    * 맨 위에 @RequiredArgsConstructor 넣고
    * private final MemberRepository memberRepository;
    * 해놓으면 알아서 생성자 해준다.
    *
    * */
    /**
     * 회원가입
     */
    @Transactional //data 쓰기에는 readonly안먹도록 어노테이션 따로 넣어준다. //변경
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }
    private void validateDuplicateMember(Member member) { //중복회원이라면 터쳐버리자
        List<Member> findMembers =
                memberRepository.findByName(member.getName()); //조회 해보자
        if (!findMembers.isEmpty()) { //조회했는데 뭔가 있어? 그럼 중복임
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }
    public Member findOne(Long memberId) { //회원 한명만 조회한다.
        return memberRepository.findOne(memberId);
    }
}
