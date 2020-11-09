package com.lg.microservice.repository;

import com.lg.microservice.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if(item.getId()==null){ //아이템은 처음엔 아이디가 없음 jpa가 제공하는 persist사용한다.
            //id없다? = 새로 생성하는 객체라는 뜻이다
            //신규 등록이라는 뜻
            em.persist(item);
        } else{ //이미 있어? 그럼 save가 아니라 update개념
            em.merge(item); //merge : update개념..?(뒤에서 다시 설명)
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){ //단건은 find할 수 있지만 여러개는 쿼리 쓰자.
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
