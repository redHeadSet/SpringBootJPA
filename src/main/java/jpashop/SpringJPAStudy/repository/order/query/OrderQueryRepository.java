package jpashop.SpringJPAStudy.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;


    public List<OrderQueryDto> findOrderQueryDtos() {
        // JPQL 은 컬렉션 데이터를 바로 넣을 순 없다
        List<OrderQueryDto> result = findOrders();

        result.forEach(o -> {
            List<OrderItemQueryDto> ordersItems = findOrdersItems(o.getOrderId());
            o.setOrderItems(ordersItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrdersItems(Long orderId) {
        return em.createQuery(
                "select new jpashop.SpringJPAStudy.repository.order.query.OrderItemQueryDto" +
                        "(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery("select new jpashop.SpringJPAStudy.repository.order.query.OrderQueryDto" +
                "(o.id, m.name, o.orderDate, o.status, d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderQueryDto.class).getResultList();
    }
}
