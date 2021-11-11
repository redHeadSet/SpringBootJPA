package jpashop.SpringJPAStudy.api;

import jpashop.SpringJPAStudy.domain.Order;
import jpashop.SpringJPAStudy.domain.OrderSearch;
import jpashop.SpringJPAStudy.repository.OrderRepository;
import jpashop.SpringJPAStudy.repository.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<OrderSimpleQueryDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderSimpleQueryDto> collect = orders.stream()
                .map(o -> new OrderSimpleQueryDto(o))
                .collect(Collectors.toList());
        return collect;
    }
    // 문제 1 : v1, v2 모두 과도한 쿼리 발생 - v2: 1+N의 쿼리 발생(지연로딩으로 인한)
    // 해결 1 : fetch 조인을 사용하여 개선해야 함 (v3)

    @GetMapping("/api/v3/simple-orders")
    public List<OrderSimpleQueryDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream()
                     .map(o -> new OrderSimpleQueryDto(o))
                     .collect(Collectors.toList());
    }
    // 쿼리 1번 나감!
    // 장점 : v4에 비해 자유도가 높음 (재사용성 높음)
    // 단점 : 모든 엔티티 값을 가져오면서 DTO에 비해 필요없는 데이터가 많음

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){
        return orderRepository.findOrderDtos();
    }
    // JPA 에서 바로 DTO로 데이터를 입력
    // SQL 짜듯이 필요한 데이터만 가지고 옴 (new 를 사용하여 데이터를 DTO로 바로 변환)
    // 장점 : 성능 최적화 상으로 좋음
    // 단점 : 재사용성이 매우 떨어짐
    //       Repository에 API 스펙 자체가 들어가게 됨

    // v3, v4의 trade off 관계 - 성능 vs 사용성
    // 요즘 성능이 좋아서 사실 v3과 v4의 차이가 크지 않음
    //  -> 하지만 DataSize가 매우 큰 경우, 트래픽이 매우 큰 경우, v4가 나을 수 있음
    // 대부분 join 에서 성능을 먹기 때문에...

    // Repository 아래 query 패키지를 만들어서 별도로 처리
    // OrderSimpleRepository를 별도로 구성, findOrderDtos 함수 자체를 이동
    // - 복잡하고 성능이 필요한 부분, 화면에 종속적으로 필요한 부분 등을 아예 분리시켜 처리
}
