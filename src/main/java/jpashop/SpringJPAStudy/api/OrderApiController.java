package jpashop.SpringJPAStudy.api;

import jpashop.SpringJPAStudy.domain.*;
import jpashop.SpringJPAStudy.repository.OrderRepository;
import jpashop.SpringJPAStudy.repository.order.query.OrderQueryDto;
import jpashop.SpringJPAStudy.repository.order.query.OrderQueryRepository;
import jpashop.SpringJPAStudy.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

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
    // ->   DB 상황은 동일하게 결과가 4개로 전송됨
    //      JPA에서 4개의 데이터 중 결과 주소값을 확인하여 동일 데이터를 제거
    // 단점 : 페이징 불가 : rows 수를 정확히 알 수 없음
    //      해당 쿼리를 페이징 쿼리를 제외하여 전달한 후, 메모리에서 페이징을 처리함
    //      데이터가 많은 경우, 메모리 부하가 심각해짐
    // + 컬렉션 패치 조인은 1개만 가능 (일대다) -> 일대다대다 정도까지 가면 데이터 부정확

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);// 아래 1번 처리
        // 2번 처리 : application.yml 에 default_batch_fetch_size 추가
        // 2번 처리 후 OrderItem 쿼리 join 시 "~ order_id in (4, 11)" 가 들어감 - LAZY 조인 시 한 번에 처리하는 갯수
        // 그 후 OrderItem 에서 Item 쿼리 시 "~ item_id in (2, 3, 9, 10)" 가 들어가면서 총 쿼리 3번으로 처리됨
        return orders.stream().map(order -> new OrderDto(order)).collect(Collectors.toList());
    }
    // 대부분의 페이징 + 컬렉션 엔티티 문제는 이 방법으로 해결 가능
    //      1. ToOne 관계는 모두 fetch join 처리 - 그렇게 처리해도 데이터 row가 늘어나지 않음
    //      2. ToMany 관계(컬렉션)는 지연 로딩
    //      3. 지연 로딩을 최적화(hibernate.default_batch_fetch_size 또는 @BatchSize)

    // cf. default_batch_fetch_size는 최대 1000. 일반적으로 100~1000 사이로 추천
    // 쿼리는 100으로 10번, 1000으로 했을 때 1번 나가기 때문에 1000이 나을 수 있지만
    // 1000으로 한 경우 어플리케이션에 의한 DB 부하가 순간적으로 크기 때문에 조율해줘야 한다

    // ============================================================================
    // JPA에서 DTO 직접 조회

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }
    // simple과 같은 기본 조회 방식
    // DTO 내 컬렉션을 바로 조회할 방법은 없다. - 그대로 조회한 후, 각 컬렉션 데이터에 대해 다시 조회
    // 즉, 결과적으로 1+N 쿼리 발생

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
