package project.market.member;

import org.springframework.data.jpa.repository.JpaRepository;
import project.market.member.Entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findByEmail(String email);
}
