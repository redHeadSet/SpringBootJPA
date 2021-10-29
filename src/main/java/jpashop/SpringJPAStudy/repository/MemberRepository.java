package jpashop.SpringJPAStudy.repository;

import jpashop.SpringJPAStudy.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository // componant scan에 의해 자동으로 SpringBean으로 인식됨
@RequiredArgsConstructor
public class MemberRepository {
//    @PersistenceContext // 표준 어노테이션, Spring이 EntityManager를 만들어서 알아서 처리함
//    @Autowired // SpringBoot 에서 지원, 원래는 위의 PersistenceContext를 써야 하는 것이 맞음
    private final EntityManager em;

//    public MemberRepository(EntityManager em) {
//        this.em = em;
//    }

//    @PersistenceUnit
//    private EntityManagerFactory emf;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
