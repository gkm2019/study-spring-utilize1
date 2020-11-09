package com.lg.microservice.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name="delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch=FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    //enum type defualt가 오디너리(숫자) 들어간다..
    //중간에 다른 상태가 들어가면 망한다. 꼭 string지정하자
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; //enum [REDY(준비), COMP(배송)]
}
