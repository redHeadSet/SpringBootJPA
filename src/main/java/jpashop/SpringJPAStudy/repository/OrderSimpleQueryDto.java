package jpashop.SpringJPAStudy.repository;

import jpashop.SpringJPAStudy.domain.Address;
import jpashop.SpringJPAStudy.domain.Order;
import jpashop.SpringJPAStudy.domain.OrderStatus;
import lombok.Data;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String memberName;
    private LocalDateTime orderData;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Order order) {
        orderId = order.getId();
        memberName = order.getMember().getName();
        orderData = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();
    }

    public OrderSimpleQueryDto(Long orderId,
                               String memberName,
                               LocalDateTime orderData,
                               OrderStatus orderStatus,
                               Address address) {
        this.orderId = orderId;
        this.memberName = memberName;
        this.orderData = orderData;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
