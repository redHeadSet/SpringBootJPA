package jpashop.SpringJPAStudy.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 연관 관계 편의 메서드
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // 비즈니스 로직 추가
    // 주문 생성이 복잡하기 때문에 별도의 생성 메서드로 처리
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem: orderItems)
            order.addOrderItem(orderItem);
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    // 주문 취소
    public void cancel() {
        if(getDelivery().getStatus() == DeliveryStatus.COMP)
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니다");

        setStatus(OrderStatus.CANCEL);

        for(OrderItem orderItem : getOrderItems()){
            orderItem.cancel();
        }
    }

    // 가격  조회
    public int getTotalPrice(){
//        int total_price = 0;
//        for(OrderItem orderItem : getOrderItems())
//            total_price += orderItem.getTotalPrice();
//        return total_price;
        // 코드 작성 후, for 문 앞에서 Alt+Enter : Replace with Sum()
        // inline 단축키는 Ctrl+Alt+N

        return getOrderItems().stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
