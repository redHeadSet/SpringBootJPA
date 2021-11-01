package jpashop.SpringJPAStudy.service;

import jpashop.SpringJPAStudy.domain.Address;
import jpashop.SpringJPAStudy.domain.Member;
import jpashop.SpringJPAStudy.domain.Order;
import jpashop.SpringJPAStudy.domain.OrderStatus;
import jpashop.SpringJPAStudy.domain.item.Book;
import jpashop.SpringJPAStudy.domain.item.Item;
import jpashop.SpringJPAStudy.exception.NotEnoughStockException;
import jpashop.SpringJPAStudy.repository.ItemRepository;
import jpashop.SpringJPAStudy.repository.MemberRepository;
import jpashop.SpringJPAStudy.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private MemberService memberService;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ItemService itemService;
    @Autowired private ItemRepository itemRepository;

    private Item createBook(String name, int price, int stockQuantity) {
        Item item = new Book();
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        itemRepository.save(item);
        return item;
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("도시", "거리", "301020"));
        memberRepository.save(member);
        return member;
    }

    @Test
    public void 상품주문(){
        // given
        Member member = createMember("Tester1");
        Item item = createBook("book1", 10000, 10);

        int orderCount = 2;

        // when
        Long order_id = orderService.order(member.getId(), item.getId(), orderCount);

        // then
        Order find_order = orderRepository.findOne(order_id);

        assertEquals("상품 주문 시 상태", OrderStatus.ORDER, find_order.getStatus());
        assertEquals("상품 주문 종류", 1, find_order.getOrderItems().size());
        assertEquals("상품 주문 가격", 10000*orderCount, find_order.getTotalPrice());
        assertEquals("상품 주문 재고", 10-orderCount, item.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 재고초과() {
        // given
        Member member = createMember("Tester1");
        Item item = createBook("book1", 10000, 10);

        int orderCount = 11;

        // when
        Long order_id = orderService.order(member.getId(), item.getId(), orderCount);

        // then
        fail("주문이 초과 주문 되었습니다");
    }

    @Test
    public void 주문취소(){
        // given
        Member member = createMember("Tester1");
        Item item = createBook("book1", 10000, 10);
        int order_count = 2;
        Long order_id = orderService.order(member.getId(), item.getId(), order_count);
        Order ordered = orderRepository.findOne(order_id);

        // when
        orderService.cancelOrder(order_id);

        // then
        assertEquals("취소 상태", OrderStatus.CANCEL, ordered.getStatus());
        assertEquals("취소 템 수량", 10, item.getStockQuantity());
    }

}