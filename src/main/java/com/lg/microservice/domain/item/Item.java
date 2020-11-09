package com.lg.microservice.domain.item;

import com.lg.microservice.domain.Category;
import com.lg.microservice.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//상속 전략 : singleTable : 한 테이블에 다 넣는것
//한 테이블에 책/음반/영화 다 넣는것임
@DiscriminatorColumn(name="dtype")
//dtype : discriminatorValue로 구분하면 된다.
@Getter @Setter
public abstract class Item {
    @Id @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items") //카테코리가 연관관계의 주인이다.
    private List<Category> categories = new ArrayList<Category>();

    //==비즈니스 로직==//
    public void addStock(int quantity) { //재고 수량 증가
        this.stockQuantity += quantity;
    }
    public void removeStock(int quantity) { //재고 줄이기
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock"); //예외 따로 만들기
        }
        this.stockQuantity = restStock;
    }
}
