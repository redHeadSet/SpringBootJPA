package jpashop.SpringJPAStudy.Controller;

import jpashop.SpringJPAStudy.domain.Address;
import jpashop.SpringJPAStudy.domain.Member;
import jpashop.SpringJPAStudy.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm memberForm, BindingResult result) {
        // @Valid 를 하면 MemberForm 내 적용된 Validation 을 확인하여 처리한다
        // 즉, 현재 name이 비어있으면 오류가 남

        // public String create(@Valid MemberForm memberForm) {
        // 주석처럼 BindingResult 가 없다면 에러 발생 시 whitelabel 에러 페이지로 간다.
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }
        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());
        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }
}
