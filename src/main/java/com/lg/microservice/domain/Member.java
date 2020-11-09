package com.lg.microservice.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String name;

    @Embedded //내장 타입이라는 뜻
    //embedded나 embedable하나만 있어도 됨
    private Address address;

    @OneToMany(mappedBy = "member")
    //하나의 회원이 여러개의 상품 주문한다. 1대 다
    //연관관계의 주인이 아니다! mappedBy
    //order table에 있는 member field(변수)에 의해 맵핑이 된거임
    private List<Order> orders = new ArrayList<>();
}
