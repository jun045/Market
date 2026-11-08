package project.market.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import project.market.PageResponse;
import project.market.member.Entity.Member;
import project.market.member.dto.*;
import project.market.member.enums.Level;
import project.market.member.enums.MemberStatus;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    //회원가입
    @PostMapping("/api/v1/members")
    public UserSignupResponse signupUser (@Valid @RequestBody CreateUserSignupRequest request){

        return memberService.createUser(request);
    }

    //로그인
    @PostMapping("/api/v1/members/login")
    public UserLoginResponse userLogin (@RequestBody UserLoginRequest request){

        return memberService.login(request);
    }

    //회원정보 수정
    @PutMapping("/api/v1/members")
    public UpdateUserResponse updateUser (@AuthenticationPrincipal (expression = "member") Member member,
                                          @Valid @RequestBody UpdateUserRequest request){

        return memberService.update(request, member);
    }

    //회원의 자신 정보 조회
    @GetMapping("/api/v1/members/me")
    public UserDetailResponse getDetailUser(@AuthenticationPrincipal (expression = "member") Member member){

        return memberService.getUser(member);
    }

    //비밀번호 재설정
    @PatchMapping("/api/v1/members/me/password")
    public ResponseEntity<Void> changeUserPassword (@AuthenticationPrincipal (expression = "member") Member member,
                                                    @Valid @RequestBody UserPasswordRequest request){

        memberService.changePassword(request, member);

        return ResponseEntity.ok().build();
    }

    //회원탈퇴
    @DeleteMapping("/api/v1/members")
    public ResponseEntity<Void> softDeleteMember (@AuthenticationPrincipal (expression = "member") Member member,
                                                  @RequestBody DeleteUserRequest request){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(">> [Controller] principal type = " + auth.getPrincipal().getClass());
        System.out.println(">> [Controller] principal = " + auth.getPrincipal());
        memberService.deleteUser(request, member);

        return ResponseEntity.ok().build();
    }

    // 관리자용 회원 목록 조회
    // 검색 가능 (부분 검색 - loginId, email 일치 필터 : level, memberStatus)
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/api/v1/admin/users")
    public PageResponse<UserListResponse> getAllUser (@RequestParam(required = false) String loginId,
                                                      @RequestParam(required = false) String email,
                                                      @RequestParam(required = false) Level level,
                                                      @RequestParam(required = false) MemberStatus memberStatus,
                                                      @PageableDefault(size = 20) Pageable pageable){

        return memberService.getAllMember(pageable, loginId, email, level, memberStatus);

    }

}
