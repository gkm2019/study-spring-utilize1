package com.lg.microservice.sevice;

import com.lg.microservice.domain.item.Item;
import com.lg.microservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional //read아니니까 트랜잭션 추가하기
    public void saveItem(Item item){ //저장
        itemRepository.save(item);
    }

    public List<Item> findItems(){ //조회하기
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
