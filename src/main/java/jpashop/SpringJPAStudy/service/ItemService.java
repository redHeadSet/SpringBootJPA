package jpashop.SpringJPAStudy.service;

import jpashop.SpringJPAStudy.domain.item.Item;
import jpashop.SpringJPAStudy.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    final private ItemRepository itemRepository;

    @Transactional(readOnly = false)
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public Item findItem(Long id) {
        return itemRepository.findOne(id);
    }

    public List<Item> findAll(){
        return itemRepository.findAll();
    }

    @Transactional(readOnly = false)
    public Item update(Long id, String name, int price, int stockQuantity) {
        return itemRepository.update(id, name, price, stockQuantity);
    }
}
