package com.lg.microservice.domain;

import com.lg.microservice.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class OrderItem {
    @Id @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; //주문 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; //주문

    private int orderPrice;//주문 가격
    private int count; //주문 수량

    //함부러 생성되는 것을 막자 원래는 protected로 막는다.
    //하지만 lombok @NoArgsConstructor(access = AccessLevel.PROTECTED)로 적어주면 자동으로 막아줌
    //protected OrderItem() {}

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){ //어떤 item, 얼마에, 몇개 샀어~
        OrderItem orderItem = new OrderItem();// 할인같은거 적용될경우 이거 있어야함.
        orderItem.setItem(item); //어떤 item샀는지 넣고
        orderItem.setOrderPrice(orderPrice); //그 아이템 얼마짜린지 넣어 전체 가격은 밑에 함수에~
        orderItem.setCount(count); //수량 넣어

        //재고 까주기
        item.removeStock(count); //주문한 만큼 토탈 재고량 까주기
        return orderItem; //이제 주문한 아이템 정보(몇개인지 얼마인지) 내뱉어준다.
    }

    //==비즈니스 로직==//
    //주문 엔티티에서 주문서에 있는 내용을 하나 취소하면
    //주문서 안에 있는 재고들에 대한 상품 정보도 빼줘야한다. (재고를 다시 원래대로 늘려줘야함)
    public void cancel() {
        getItem().addStock(count); //재고 수량을 원래대로 돌려준다.
    }

    //==조회 로직==//
    /**주문상품 전체 가격 조회*/
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
