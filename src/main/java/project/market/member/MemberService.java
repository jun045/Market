package project.market.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.market.auth.JwtProvider;
import project.market.member.Entity.Member;
import project.market.member.dto.*;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    //회원가입
    public UserSignupResponse createUser(@Valid CreateUserSignupRequest request){
        if (memberRepository.findByLoginId(request.loginId()).isPresent()){
            throw new RuntimeException("이미 존재하는 Id입니다.");
        }
        if (memberRepository.findByEmail(request.email()).isPresent()){
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        Member member = Member.builder()
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .nickname(request.nickname())
                .email(request.email())
                .build();

        memberRepository.save(member);

        return MemberMapper.toUserSignupResponse(member);
    }

    //로그인
    public UserLoginResponse login (UserLoginRequest request){

        Member member = memberRepository.findByLoginId(request.loginId()).orElseThrow(
                () -> new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.")
        );

        if(!passwordEncoder.matches(request.password(), member.getPassword())){
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        return new UserLoginResponse(jwtProvider.createToken(member.getId(), member.getRole()));
    }

    //회원정보 변경
    @Transactional
    public UpdateUserResponse update (@Valid UpdateUserRequest request, Member member){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new RuntimeException("자신의 회원정보만 수정할 수 있습니다.")
        );

        user.updateProfile(request.name(),
                request.nickName(),
                request.email());

        return MemberMapper.toUpdateUserResponse(user);
    }

    //자신의 회원 정보 조회
    public UserDetailResponse getUser (Member member){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new RuntimeException("자신의 회원정보만 조회할 수 있습니다.")
        );

        return MemberMapper.toUserDetailResponse(user);
    }

    //비밀번호 변경
    @Transactional
    public void changePassword (@Valid UserPasswordRequest request, Member member){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new RuntimeException("자신의 회원정보만 수정할 수 있습니다.")
        );

        if(!passwordEncoder.matches(request.existingPassword(), user.getPassword())){
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        if(!request.newPassword().equals(request.confirmNewPassword())){
            throw new RuntimeException("새 비밀번호가 일치하지 않습니다.");
        }

        user.changePassword(passwordEncoder.encode(request.confirmNewPassword()));
    }

    //회원탈퇴(Soft Delete)
    @Transactional
    public void deleteUser (DeleteUserRequest request, Member member){

        Member user = memberRepository.findById(member.getId()).orElseThrow(
                () -> new RuntimeException("자신의 계정만 탈퇴할 수 있습니다.")
        );

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new RuntimeException("비밀번호를 다시 입력해 주세요");
        }

        user.softDelete();
    }

}
