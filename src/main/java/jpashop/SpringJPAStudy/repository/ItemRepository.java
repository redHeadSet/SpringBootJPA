package jpashop.SpringJPAStudy.repository;

import jpashop.SpringJPAStudy.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item){
        if(item.getId() == null)
            em.persist(item);   // INSERT
        else
            em.merge(item);     // UPDATE
    }

    public Item update(Long id, String name, int price, int stockQuantity) {
        Item item = findOne(id);

        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        return item;
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
