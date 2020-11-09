package com.lg.microservice.repository;

import com.lg.microservice.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository //spring bean 으로 등록된다.
@RequiredArgsConstructor
public class MemberRepository {
    
    //@PersistenceContext //jpa 표준 어노테이션 , RequiredArgsConstructor 어노테이션을 선언했기 때문에 final붙여서 생성자 주입
    private final EntityManager em;
    
    public void save(Member member) {
        em.persist(member); //db에 트랜잭션 commit되는 순간에서야 저장된다. persist(영속성)
    }
    public Member findOne(Long id) {
        return em.find(Member.class, id); //member (id) 찾아서 반환해준다. 단건 조회 pk id를 넣어준다.
    }
    public List<Member> findAll() { //회원 전체 조회
        /*inline전
        * List<Member> result = em.createQuery("select m from Member m", Member.class)
        * .getResultList();
        * 
        * //member table 에 있는 모든 정보 조회
        * */
        return em.createQuery("select m from Member m", Member.class) //jpql 이다. entity객체를 대상으로 쿼리가 작성된다.
                .getResultList(); //파라미터가 바인딩 된다.
    }
    public List<Member> findByName(String name) {//회원을 이름으로 검색
        return em.createQuery("select m from Member m where m.name = :name",
                Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
