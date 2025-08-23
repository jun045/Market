package project.market.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.market.member.MemberRepository;
import project.market.member.Entity.Member;

@RequiredArgsConstructor
@Service
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        return new MemberPrincipal(member);
    }

    public UserDetails loadUserById(Long id){
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("회원을 찾을 수 없습니다.")
        );

        return new MemberPrincipal(member);
    }
}
