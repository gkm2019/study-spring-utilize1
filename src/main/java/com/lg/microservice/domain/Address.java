package com.lg.microservice.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable //어딘가에 내장 될 수 있다.
@Getter //@Setter
//setter 지워버렸으니까 변경 불가능함
//임베디드 타입 @Embeddable은 자바 기본 생성자를 public 또는
//protected로 설정 해야한다. public < protected 더 안전,
//가급적 Setter사용하지않기
//즉시 로딩 EAGER : 예측 어렵다. 어떤 sql 실행될지 추적 어려움
//지연로딩 LAZY : 실무에서 모든 연관관계에 쓰임
//연관 Entity함께 조회할 경우, fetch join 또는 entity그래프 사용
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address(){} //함부러 new 하면 안되니까 protected

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
