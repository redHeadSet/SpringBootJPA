package jpashop.SpringJPAStudy.api;

import jpashop.SpringJPAStudy.domain.Address;
import jpashop.SpringJPAStudy.domain.Order;
import jpashop.SpringJPAStudy.domain.OrderSearch;
import jpashop.SpringJPAStudy.domain.OrderStatus;
import jpashop.SpringJPAStudy.repository.OrderRepository;
import jpashop.SpringJPAStudy.service.ItemService;
import jpashop.SpringJPAStudy.service.MemberService;
import jpashop.SpringJPAStudy.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        return orderRepository.findAllByString(new OrderSearch());
    }
    // 문제 1 : 무한 반복 - order 안에 member 안에 order 안에 member 안에 ...
    // 방안 1 : 둘 중 하나를 JsonIgnore 해주면 됨 (양방향이 걸리는 모든 곳에)
    // 문제 2 : 방안 1 처리 후 Lazy 로딩으로 인한 proxy 에러 발생 (ByteBuddyInterceptor)
    // 방안 2 : hibernate의 모듈을 설치 - Hibernate5Module (build.gradle) 및 Bean 등록
    //         지연 로딩을 무조건 null로 처리하여 확인
    // 문제 3 : 위 처리 시 member, delivery 가 모두 null 처리
    // 방안 3 : 강제 지연 로딩 설정 - hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
    // 문제 4 : 엄청나게 쿼리가 나감 - LAZY 처리 된 모든 것이 무조건 로딩됨 & 엔티티 스펙 노출
    // 방안 4 : 위의 강제 로딩 삭제 후, List<Order> 각각에 대해 member, delivery 를 조회하여 LAZY 로딩 객체를 초기화 시킴

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> collect = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }
    // 문제 1 : v1, v2 모두 과도한 쿼리 발생 - v2: 1+N의 쿼리 발생(지연로딩으로 인한)
    // 해결 1 : fetch 조인을 사용하여 개선해야 함 (v3)

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream()
                     .map(o -> new SimpleOrderDto(o))
                     .collect(Collectors.toList());
    }
    // 쿼리 1번 나감!
    // 문제 1 : 모든 엔티티 값을 가져오면서 DTO 에 비해 필요없는 데이터가 많음 (v4 개선)

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String memberName;
        private LocalDateTime orderData;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            memberName = order.getMember().getName();
            orderData = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
