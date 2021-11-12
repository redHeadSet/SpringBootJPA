package jpashop.SpringJPAStudy.api;

import jpashop.SpringJPAStudy.domain.*;
import jpashop.SpringJPAStudy.repository.OrderRepository;
import jpashop.SpringJPAStudy.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
//        for (Order o : orders) {
//            o.getMember();
//            o.getDelivery().getStatus();
//
//        }
        // simple 쪽 예제와 동일해서 패스..
        return orders;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }
    // OrderItem으로 일대다 조인 발생

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> allWithItem = orderRepository.findAllWithItem();
//        for (Order order : allWithItem) {
//            System.out.println("order red" + order);
//        }
        return allWithItem.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());
    }
    // 해당 방법으로 join 처리 시 각 Order에 대해 2줄씩 생성됨 - orderId:4-2줄, 11-2줄
    // 각 order에 대해 orderItem 이 2개씩 들어있어 발생한 문제 (Order과 OrderItem의 Join 처리 시 발생)
    // + distinct 사용하여 중복 제거
    // ->   DB 상황은 동일하게 결과가 4개지만,
    //      JPA에서 4개의 데이터 중 동일한 데이터가 있다는 걸 판단한 후 2개로 줄여줌 (결과 주소값 확인)
    // 단점 : 페이징 불가 : rows 수를 정확히 알 수 없음
    //      해당 쿼리를 페이징 쿼리를 제외하여 전달한 후, 메모리에서 페이징을 처리함
    //      데이터가 많은 경우, 메모리 부하가 심각해짐
    // + 컬렉션 패치 조인은 1개만 가능 (일대다) -> 일대다대다 정도까지 가면 데이터 부정확

    // ============================================================================
    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItemDtos;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItemDtos = order.getOrderItems().stream()
                    .map(o -> new OrderItemDto(o))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto {
        private Long orderItemId;
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            orderItemId = orderItem.getId();
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
