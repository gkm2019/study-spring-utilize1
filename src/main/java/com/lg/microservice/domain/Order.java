package com.lg.microservice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders") //테이블 명
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //생성자 접근 막는다. 누군가 order함부러 생성 못한다.
public class Order {
    @Id @GeneratedValue
    @Column(name="order_id") //컬럼 선택!
    private Long id;

    //order - member 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id") //조인할 컬럼
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    //many니까 orderItems 여러개 list로 가져옴
    //cascade :
    //persist(orderItemA)
    //persist(orderItemB)
    //persist(order)
    //이렇게 저장했어야 했지만 cascade를 붙이면 persist(order) 한줄만 해도 된다.
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; //배송정보

    private LocalDateTime orderDate; //주문시간

    //string 지정해주기
    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문 상태 [order, cancel]

    //==연관관계 메서드==//
    /*
    * 원래대로라면
    * Member member = new Member()
    * Order order = new Order()
    *
    * member.getOrders().add(order)
    * order.setMember(member);
    * 이렇게 넣어야했을텐데.
    * */
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this); //this는 order~
    }
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem); //우선 orderItem을 넣고
        orderItem.setOrder(this); //item이 들어간 order를 set한다.
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for(OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem); //현재 생성한 order에다가 order item을 하나씩 다 넣어준다.
        }

        order.setStatus(OrderStatus.ORDER); //주문 상태 넣는다.
        order.setOrderDate(LocalDateTime.now()); //주문한 시간을 넣는다.
        return order;
    }

    //==비즈니스 로직==//
    /** 주문 취소 */
    //1. 배송이 완료된 상품은 취소 불가능하게 한다.
    //2. 주문을 취소하면 총 재고량은 다시 늘어난다.
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) { //comp : 배송완료 상태
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) { //
            orderItem.cancel(); //cancle함수는.. orderitem entity안에 cancel()함수가 있다. 그 함수에서 재고 수량을 원래대로 돌려준다.
        }
    }

    //==조회 로직==//
    /** 전체 주문 가격 조회 */
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice +=orderItem.getTotalPrice();
            //왜 주문한 item에서 가져오냐?
            //이유는 : 한번 주문할때 그 안에 주문가격, 수량이 적혀있기 때문이다... 상품이 아니라 주문!! 관점이다!
            //order는 여러개의 아이템들을 주문한걸 보여주고
            //orderitem은 하나의 상품 주문 정보를 담고있다.
            //즉, orderitem에는 item 얼마짜리? 몇개? 주문했냐 이걸 담고있음
        }

        return totalPrice;
    }
}
