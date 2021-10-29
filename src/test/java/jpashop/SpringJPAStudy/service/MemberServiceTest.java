package jpashop.SpringJPAStudy.service;

import jpashop.SpringJPAStudy.domain.Member;
import jpashop.SpringJPAStudy.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    private MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception{
        // given
        Member member = new Member();
        member.setName("memberA");

        // when
        Long id = memberService.join(member);

        // then
        assertEquals(member, memberService.findOne(id));
    }

    @Test(expected = IllegalStateException.class)   // IllegalStateException 발생 시 정상 처리
    public void 중복_회원_제외() throws Exception{
        // given
        Member member1 = new Member();
        member1.setName("same name");

        Member member2 = new Member();
        member2.setName("same name");

        // when
        memberService.join(member1);
        memberService.join(member2); // Error : 상위 어노테이션(Test - expected)에서 처리
//        try {
//            memberService.join(member2); // Error
//        }catch (IllegalStateException e){
//            System.out.println(e.getMessage());
//            return;
//        }

        // then
        fail("예외 발생 해야하는데 안됨!");
    }
}