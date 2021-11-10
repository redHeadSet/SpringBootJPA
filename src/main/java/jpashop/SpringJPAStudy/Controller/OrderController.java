package jpashop.SpringJPAStudy.Controller;

import jpashop.SpringJPAStudy.domain.Member;
import jpashop.SpringJPAStudy.domain.Order;
import jpashop.SpringJPAStudy.domain.OrderSearch;
import jpashop.SpringJPAStudy.domain.item.Book;
import jpashop.SpringJPAStudy.domain.item.Item;
import jpashop.SpringJPAStudy.service.ItemService;
import jpashop.SpringJPAStudy.service.MemberService;
import jpashop.SpringJPAStudy.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findAll();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/order")
    public String createForm(@RequestParam("memberId") Long memberId,
                             @RequestParam("itemId") Long itemId,
                             @RequestParam("count") int count) {
        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        List<Order> orders = orderService.findAllByString(orderSearch);
//        List<Order> orders = new ArrayList<>();
        model.addAttribute("orders", orders);
        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String orderCancel(@PathVariable("orderId") Long orderId, Model model) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
