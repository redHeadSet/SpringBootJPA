package jpashop.SpringJPAStudy.api;

import jpashop.SpringJPAStudy.domain.Member;
import jpashop.SpringJPAStudy.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// @Controller
// @ResponseBody // json, xml 등으로 통신할 때 사용하는 어노테이션
@RestController // 위의 2개의 어노테이션을 합친 것
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMember(@RequestBody // json 데이터를 Member로 바꾸는 역할
                                           @Valid Member member) {
        // 엔티티를 지금과 같이 직접 처리하는 것은 문제가 발생할 여지가 있음 - 엔티티를 외부에 노출
        // api 수정 or Member class 수정 시 api 스펙 자체가 바뀌게 되면서 여러 사이드이펙트 생길 수 있음
        // 즉, DTO 필요
        Long joinId = memberService.join(member);
        return new CreateMemberResponse(joinId);
    }

    // 별도의 DTO 사용
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMember(@RequestBody @Valid CreateMemberRequest memberRequest){
        Member member = new Member();
        member.setName(memberRequest.getName());

        memberService.join(member);

        return new CreateMemberResponse(member.getId());
    }

    // 회원 수정 - PUT 으로 생성
    @PutMapping("/api/v2/members/{memberId}")
    public UpdateMemberResponse editMember(@PathVariable("memberId") Long memberId,
                                           @RequestBody @Valid UpdateMemberRequest memberRequest) {
        // update 문 안에서 아래 2개를 다 처리 가능하지만, 의미를 위해 별도로 작성
        Long updatedId = memberService.updateMember(memberId, memberRequest.getName());
        Member updateMember = memberService.findOne(updatedId);

        return new UpdateMemberResponse(updateMember.getId(), updateMember.getName());
    }

    // 회원 조회 - 엔티티 직접 전달
    @GetMapping("/api/v1/members")
    public List<Member> v1memberList(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result<SelectMemberDto> v2memberList(){
        List<Member> findMembers = memberService.findMembers();
        List<SelectMemberDto> collect = findMembers.stream()
                .map(m -> new SelectMemberDto(m.getId(), m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor // 생성자 자동 처리
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class SelectMemberDto{
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private Integer count;
        private T data;
    }
}
