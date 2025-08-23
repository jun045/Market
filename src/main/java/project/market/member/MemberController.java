package project.market.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import project.market.member.Entity.Member;
import project.market.member.dto.*;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    //회원가입
    @PostMapping("api/v1/members")
    public UserSignupResponse signupUser (@RequestBody CreateUserSignupRequest request){

        return memberService.createUser(request);
    }

    //로그인
    @PostMapping("api/v1/members/login")
    public UserLoginResponse userLogin (@RequestBody UserLoginRequest request){

        return memberService.login(request);
    }

    //회원정보 수정
    @PutMapping("api/v1/members")
    public UpdateUserResponse updateUser (@AuthenticationPrincipal (expression = "member") Member member,
                                          @RequestBody UpdateUserRequest request){

        return memberService.update(request, member);
    }

    //회원의 자신 정보 조회
    @GetMapping("api/v1/members/me")
    public UserDetailResponse getDetailUser(@AuthenticationPrincipal (expression = "member") Member member){

        return memberService.getUser(member);
    }

    //비밀번호 재설정
    @PatchMapping("api/v1/members/me/password")
    public ResponseEntity<Void> changeUserPassword (@AuthenticationPrincipal (expression = "member") Member member,
                                                    @RequestBody UserPasswordRequest request){

        memberService.changePassword(request, member);

        return ResponseEntity.ok().build();
    }

    //회원탈퇴
    @DeleteMapping("api/v1/members")
    public ResponseEntity<Void> softDeleteMember (@AuthenticationPrincipal (expression = "member") Member member,
                                                  @RequestBody DeleteUserRequest request){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(">> [Controller] principal type = " + auth.getPrincipal().getClass());
        System.out.println(">> [Controller] principal = " + auth.getPrincipal());
        memberService.deleteUser(request, member);

        return ResponseEntity.ok().build();
    }

}
