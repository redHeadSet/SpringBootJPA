package jpashop.SpringJPAStudy.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    // ==========================================================
    public List<OrderQueryDto> findAllByDtos_opti() {
        List<OrderQueryDto> result = findOrders();

        Map<Long, List<OrderItemQueryDto>> collect = findOrderItemMap(toOrderIds(result));

        result.forEach(o -> {
            o.setOrderItems(collect.get(o.getOrderId()));
        });

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new jpashop.SpringJPAStudy.repository.order.query.OrderItemQueryDto" +
                                "(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds"
                        , OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> collect = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        return collect;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());
    }

    // ==========================================================
    public List<OrderFlatDto> findAllByDtos_flat() {
        return em.createQuery(
                "select new jpashop.SpringJPAStudy.repository.order.query.OrderFlatDto" +
                        "(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
