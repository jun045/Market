package project.market;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import project.market.auth.JwtProvider;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.dto.*;
import project.market.member.enums.MemberStatus;
import project.market.member.enums.Role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class MemberTest extends AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp(){
        RestAssured.port = port;
        databaseCleanup.execute();

        Member admin = Member.builder()
                .loginId("admin")
                .password(passwordEncoder.encode("aAbB1234567890!"))
                .name("관리자")
                .nickname("관리자")
                .email("admin@example.com")
                .role(Role.SELLER)
                .memberStatus(MemberStatus.ACTIVE)
                .isDeleted(false)
                .build();

        Member user1 = Member.builder()
                .loginId("user1")
                .password(passwordEncoder.encode("aAbB1234567890!"))
                .name("유저1")
                .nickname("유저1")
                .email("user1@example.com")
                .role(Role.BUYER)
                .memberStatus(MemberStatus.ACTIVE)
                .isDeleted(false)
                .build();

        Member user2 = Member.builder()
                .loginId("user2")
                .password(passwordEncoder.encode("aAbB1234567890!"))
                .name("유저2")
                .nickname("유저2")
                .email("user2@example.com")
                .role(Role.BUYER)
                .memberStatus(MemberStatus.ACTIVE)
                .isDeleted(false)
                .build();

        memberRepository.save(admin);
        memberRepository.save(user1);
        memberRepository.save(user2);

        tokens.put("admin", jwtProvider.createToken(admin.getId(), admin.getRole()));
        tokens.put("user1", jwtProvider.createToken(user1.getId(), user1.getRole()));
        tokens.put("user2", jwtProvider.createToken(user2.getId(), user2.getRole()));
    }

    private Map<String, String> tokens = new HashMap<>();
    private String getToken(String loginId){
        return tokens.get(loginId);
    }

    @Test
    @DisplayName("회원가입")
    public void 회원가입 (){
        UserSignupResponse userSignupResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new CreateUserSignupRequest("user3", "aAbB1234567890!", "유저3", "유저3_n", "user3@example.com"))
                .when()
                .post("api/v1/members")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserSignupResponse.class);

        assertThat(userSignupResponse.loginId()).isEqualTo("user3");
        assertThat(userSignupResponse.name()).isEqualTo("유저3");

    }

    @DisplayName("회원정보 수정")
    @Test
    public void 회원정보수정(){
        UpdateUserResponse updateUserResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("admin"))
                .body(new UpdateUserRequest("수정user1", "수정user1@example.com", "수정user1_n"))
                .when()
                .put("api/v1/members")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UpdateUserResponse.class);

        assertThat(updateUserResponse.name()).isEqualTo("수정user1");
        assertThat(updateUserResponse.email()).isEqualTo("수정user1@example.com");
    }

    @DisplayName("회원정보 조회")
    @Test
    public void 회원정보조회 (){
        UserDetailResponse userDetailResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("user1"))
                .when()
                .get("api/v1/members/me")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserDetailResponse.class);

        assertThat(userDetailResponse.name()).isEqualTo("유저1");
        assertThat(userDetailResponse.nickname()).isEqualTo("유저1");
    }

    @DisplayName("비밀번호 재설정")
    @Test
    public void 비밀번호재설정(){
        //비밀번호 재설정
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("user1"))
                .body(new UserPasswordRequest("aAbB1234567890!", "aAbB1234567890#", "aAbB1234567890#"))
                .when()
                .patch("api/v1/members/me/password")
                .then().log().all()
                .statusCode(200);

        //재설정된 비밀번호로 로그인
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "aAbB1234567890#"))
                .when()
                .post("api/v1/members/login")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("회원 탈퇴")
    @Test
    public void 회원탈퇴(){
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("user1"))
                .body(new DeleteUserRequest("aAbB1234567890!"))
                .when()
                .delete("api/v1/members")
                .then().log().all()
                .statusCode(200);

    }

    @DisplayName("loginId로 회원 부분검색")
    @Test
    public void loginId_부분검색(){
        //given : setUp에 admin, user1, user2 존재
        //when : "user" 로 검색
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("admin"))
                .queryParam("loginId", "user")
                .when()
                .get("api/v1/admin/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

        //then : user1, user2 만 조회되고 admin 은 제외된다
        List<UserListResponse> users = response
                .jsonPath()
                .getList("content", UserListResponse.class);

        int totalElements = response.jsonPath().getInt("pageInfo.totalElement");

        assertThat(users).hasSize(2);
        assertThat(totalElements).isEqualTo(2);
        assertThat(users).extracting("loginId")
                .containsExactlyInAnyOrder("user1", "user2");
        assertThat(users).extracting("loginId")
                .allMatch(loginId -> ((String) loginId).contains("user"));
    }

    @DisplayName("email로 회원 부분검색")
    @Test
    public void email_부분검색(){
        //when : "user1@" 로 검색
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("admin"))
                .queryParam("email", "user1@")
                .when()
                .get("api/v1/admin/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

        //then : user1 만 조회된다
        List<UserListResponse> users = response
                .jsonPath()
                .getList("content", UserListResponse.class);

        int totalElements = response.jsonPath().getInt("pageInfo.totalElement");

        assertThat(users).hasSize(1);
        assertThat(totalElements).isEqualTo(1);
        assertThat(users.get(0).loginId()).isEqualTo("user1");
    }

    @DisplayName("검색조건이 없으면 전체 회원이 조회된다")
    @Test
    public void 검색조건없으면_전체조회(){
        //when : 조건 없이 조회
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("admin"))
                .when()
                .get("api/v1/admin/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

        //then : admin, user1, user2 전체 조회
        int totalElements = response.jsonPath().getInt("pageInfo.totalElement");
        assertThat(totalElements).isEqualTo(3);
    }

    @DisplayName("매칭되는 회원이 없으면 빈 결과가 조회된다")
    @Test
    public void 검색_매칭없음(){
        //when : 존재하지 않는 loginId 로 검색
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("admin"))
                .queryParam("loginId", "존재하지않는아이디")
                .when()
                .get("api/v1/admin/users")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

        //then
        List<UserListResponse> users = response
                .jsonPath()
                .getList("content", UserListResponse.class);

        int totalElements = response.jsonPath().getInt("pageInfo.totalElement");

        assertThat(users).isEmpty();
        assertThat(totalElements).isZero();
    }


}