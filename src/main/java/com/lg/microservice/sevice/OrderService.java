package com.lg.microservice.sevice;

import com.lg.microservice.domain.*;
import com.lg.microservice.domain.item.Item;
import com.lg.microservice.repository.ItemRepository;
import com.lg.microservice.repository.MemberRepository;
import com.lg.microservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    /** 주문 */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) { //주문하려면, 누구인지id, 어떤 상품인지id, 몇개인지count
        //엔티티 조회
        Member member = memberRepository.findOne(memberId); //주문하려는 회원정보 가져와아햐니까 memberRepository 선언
        Item item = itemRepository.findOne(itemId); //주문하려는 item정보 가져와야 하니까 itemRepository 선언

        /**
         * delivery, orderItem << 이 두가지는 order만 참조해서 쓴다. order > [delivery/orderItem]
         * cascade 걸려있어서 따로 repository등록안하고 바로 create할 수 있다.
         * 주문에는 (주문상품들, 배송이 하나씩 걸려있음)
         * 단, 이 외에 참조가 얽혀있다면 별도로 repository만들어서 써야함 cascade안됨
         *
         * 일단 그냥 reposi생성해서 쓰다가...
         * 그냥 하다가 참조 안얽혀있으면 그때 리팩토링으로 casecade쓰자.
         */

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress()); //회원의 주소를 그냥 바로 가져옴..실제로는 배송지 정보 따로 입력함! 예제는 간단하게
        delivery.setStatus(DeliveryStatus.READY); //주문 상태는 대기

        //주문상품 생성
        //어떤 아이템인지, 아이템 가격이랑, 수량 넣어서 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        //누가, 배송정보를, 주문한 아이템
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        return order.getId(); //order 식별자만 반환해~
    }
    /** 주문 취소 */
    @Transactional
    public void cancelOrder(Long orderId) { //주문 취소할 때는 주문 id만 넘어온다.
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId); //주문 내역에서 주문 id찾아
        //주문 취소
        order.cancel();
        //찾은놈 그냥 바로 지워.. 왜 코드가 이거밖에 없지? order의 비즈니스 로직에서 예외처리 다 알아서 해주고 상태 , 재고수량 다시 돌려줌
        //<jpa의 강점!!>
        //평상시라면..? data변경했지? 그럼 update직접 날렸어야해..
        //근데 jpa는 그냥 데이터들만 바꾸면 알아서 변경 포인트를 감지하고 db에 update해준다...개꿀
    }
    /** 주문 검색 */
/*
 public List<Order> findOrders(OrderSearch orderSearch) {
 return orderRepository.findAll(orderSearch);
 }
*/
}
