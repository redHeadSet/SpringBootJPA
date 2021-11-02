package jpashop.SpringJPAStudy.service;

import jpashop.SpringJPAStudy.domain.*;
import jpashop.SpringJPAStudy.domain.item.Item;
import jpashop.SpringJPAStudy.repository.ItemRepository;
import jpashop.SpringJPAStudy.repository.MemberRepository;
import jpashop.SpringJPAStudy.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문
    @Transactional(readOnly = false)
    public Long order(Long member_id, Long item_id, int count){
        Member member = memberRepository.findOne(member_id);
        Item item = itemRepository.findOne(item_id);
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        OrderItem orderItem1 = OrderItem.createOrderItem(item, item.getPrice(), count);
        Order order = Order.createOrder(member, delivery, orderItem1);

//        OrderItem orderItem2 = OrderItem.createOrderItem(item, item.getPrice(), count);
//        Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);

        orderRepository.save(order);    // cascade 옵션으로 delivery, orderItem 이 자동으로 persist 처리
        return order.getId();
    }

    // 주문 취소
    @Transactional(readOnly = false)
    public void cancelOrder(Long order_id) {
        Order cancel_order = orderRepository.findOne(order_id);
        cancel_order.cancel();
    }

    // JPQL 검색
    public List<Order> findAllByString(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }

    // Criteria 검색
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        return orderRepository.findAllByCriteria(orderSearch);
    }
}
