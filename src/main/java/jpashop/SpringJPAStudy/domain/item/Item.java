package jpashop.SpringJPAStudy.domain.item;


import jpashop.SpringJPAStudy.domain.Category;
import jpashop.SpringJPAStudy.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items", fetch = FetchType.LAZY)
    private List<Category> categories = new ArrayList<>();
    
    // 비즈니스 로직 추가
    public void addStock(int stockQuantity){
        this.stockQuantity += stockQuantity;
    }

    public void romoveStock(int removeQuantity){
        int restStock = this.stockQuantity - removeQuantity;
        if(restStock < 0)
            throw new NotEnoughStockException("need more stock");
        this.stockQuantity = restStock;
    }
}
