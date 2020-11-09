package com.lg.microservice.sevice;

import com.lg.microservice.domain.Address;
import com.lg.microservice.domain.Member;
import com.lg.microservice.domain.Order;
import com.lg.microservice.domain.OrderStatus;
import com.lg.microservice.domain.item.Book;
import com.lg.microservice.domain.item.Item;
import com.lg.microservice.exception.NotEnoughStockException;
import com.lg.microservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @PersistenceContext EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //Given
        Member member = createMember();

        Book book = createBook("JPA", 10000, 10);

        int orderCount = 2;

        //When
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //Then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(OrderStatus.ORDER).isEqualTo(getOrder.getStatus());
        assertThat(1).isEqualTo(getOrder.getOrderItems().size()); //주문 한 상품의 수는 정확해야함
        assertThat(10000 * orderCount).isEqualTo(getOrder.getTotalPrice()); //주문 가격은 상품가격*수량 이다
        assertThat(8).isEqualTo(book.getStockQuantity()); //주문수량만큼 재고는 줄어야한다.
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //Given
        Member member = createMember();
        Item item = createBook("JPA", 10000, 10); //이름, 가격, 재고

        int orderCount = 11; //실제 재고는 10개인데 11개지? exception 터진다!

        //When
        orderService.order(member.getId(), item.getId(), orderCount);
        NotEnoughStockException e = assertThrows(NotEnoughStockException.class,
                () -> orderService.order(member.getId(), item.getId(), orderCount)); //예외 터진다.

        //Then
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member=createMember();
        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount); //주문 한 것 까지 데이터 주어진다.

        //when
        orderService.cancelOrder(orderId);

        //then
        //재고가 잘 복구 되었는가?
        Order getOrder = orderRepository.findOne(orderId);
        assertThat(OrderStatus.CANCEL).isEqualTo(getOrder.getStatus()); //주문 취소 상태가 일치해야한다.
        assertThat(10).isEqualTo(book.getStockQuantity()); //주문이 취소된 상품은 재고 다시 증가 해야한다.
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가","123-123"));
        em.persist(member);
        return member;
    }

}