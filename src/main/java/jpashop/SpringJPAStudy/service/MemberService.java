package jpashop.SpringJPAStudy.service;

import jpashop.SpringJPAStudy.domain.Member;
import jpashop.SpringJPAStudy.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 조회(읽기) 전용인 경우, 최적화가 잘 됨
// @AllArgsConstructor // Lombok의 모든 멤버 변수에 대한 생성자
@RequiredArgsConstructor // Lombok의 final 멤버 변수에 대한 생성자
public class MemberService {
    // final 로 변경하지 않도록 체크
    private final MemberRepository memberRepository;

    // 생성자 Injection -> @RequiredArgsConstructor 로 처리
//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    // 회원 가입
    @Transactional  // (readOnly = false) // default 값 - 얘는 readOnly가 아님!
    public Long join(Member member){
        validateDuplicateMember(member);

        memberRepository.save(member);
        return member.getId();
    }

    // 중복 회원 검증 - 동일 이름 제외
    // 사실 이런 경우에는 name 을 unique 제약 조건을 해야 함 : 동시에 해당 함수가 호출될 수 있기 때문
    private void validateDuplicateMember(Member member) {
        // Exception 발생
        List<Member> memberList = memberRepository.findByName(member.getName());
        if(!memberList.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 단건 조회
    public Member findOne(Long id){
        return memberRepository.findOne(id);
    }
}
