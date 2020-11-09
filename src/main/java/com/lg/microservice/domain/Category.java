package com.lg.microservice.domain;

import com.lg.microservice.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name="category_id")
    private Long id;

    private String name;

    @ManyToMany
    //1:다 - 다: 1 로 풀어내는 중간 table이 필요하다
    @JoinTable(name = "category_item",
            //joinColum : 중간테이블에 있는 카테고리 id
            //inverseJoinColums : 중간테이블에서 아이템으로 들어가는 놈
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();
    //아이템들을 가지고있다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Category parent;

    @OneToMany(mappedBy="parent")
    //카테고리의 자식은 여러개 가질 수 있다.
    //자기 자신 안에 부모있음 (바로위에 Parent)
    private List<Category> child = new ArrayList<>();

    //연관관계 메서드
    public void addChildCategory(Category child){
        //양방향 자식에 들어가면 부모에 도 들어가야하고, 부모에 들어가도 자식에도 부모 정보 들어가야함
        this.child.add(child);
        child.setParent(this);
    }
}
