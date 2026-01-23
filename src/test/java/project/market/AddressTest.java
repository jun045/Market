package project.market;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import project.market.address.AddressRepository;
import project.market.address.dto.AddressResponse;
import project.market.address.dto.CreateAddressRequest;
import project.market.address.dto.UpdateAddressRequest;
import project.market.auth.JwtProvider;
import project.market.auth.SecurityConfig;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.dto.UserLoginRequest;
import project.market.member.dto.UserLoginResponse;
import project.market.member.enums.MemberStatus;
import project.market.member.enums.Role;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class AddressTest extends AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @BeforeEach
    public void setUp(){
        RestAssured.port = port;
        databaseCleanup.execute();

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

        memberRepository.save(user1);

        String user1Token = jwtProvider.createToken(user1.getId(), user1.getRole());
        tokens.put("user1", user1Token);

        Long user1MemberId = jwtProvider.getMemberId(user1Token);
        memberIds.put("user1", user1MemberId);
    }

    private Map<String, String> tokens = new HashMap<>();
    private String getToken(String loginId){
        return tokens.get(loginId);
    }
    private Map<String, Long> memberIds = new HashMap<>();
    private Long getMemberId(String loginId){
        return memberIds.get(loginId);
    }

    @Test
    @DisplayName("주소생성 테스트")
    public void 주소생성 (){

        //로그인
        login();

        AddressResponse addressResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("user1"))
                .body(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-1234-5678", "광화문교보문고", "12345", "서울시 종로구 1", "교보생명빌딩 지하 1층", "없음", false))
                .when()
                .post("api/v1/members/me/address")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AddressResponse.class);

        assertThat(addressResponse.isDefaultedAddress()).isTrue();
    }

    @Test
    @DisplayName("주소 상세 조회 테스트")
    public void 주소상세조회 (){

        //로그인
        login();

        //주소 생성
        Long addressId = createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-1234-5678", "광화문교보문고", "12345", "서울시 종로구 1", "교보생명빌딩 지하 1층", "없음", true));

        AddressResponse addressResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("user1"))
                .pathParam("addressId", addressId)
                .when()
                .get("api/v1/members/me/address/{addressId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AddressResponse.class);
    }

    @Test
    @DisplayName("주소 목록 조회 테스트")
    public void 주소목록조회 (){

        //로그인
        login();

        //주소생성
        createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-1234-5678", "광화문교보문고", "12345", "서울시 종로구 1", "교보생명빌딩 지하 1층", "없음", true));
        createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-5678-9012", "네이버", "13561", "성남시 분당구", "정자일로 95 네이버", "없음", true));
        createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-1234-5678", "코엑스", "06164", "서울시 강남구 삼성동", "영동대로 513", "없음", true));


        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("user1"))
                .when()
                .get("api/v1/members/me/address")
                .then().log().all()
                .statusCode(200)
                .extract()
                .response();

        // 조회 Query, 응답시간
        logPerfMetric(response);
    }

    @Test
    @DisplayName("주소 수정 테스트")
    public void 주소수정 (){

        //로그인
        login();

        //주소 생성
        Long addressId = createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-1234-5678", "광화문교보문고", "12345", "서울시 종로구 1", "교보생명빌딩 지하 1층", "없음", true));

        AddressResponse addressResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("user1"))
                .pathParam("addressId", addressId)
                .body(new UpdateAddressRequest(getMemberId("user1"), "유저1", "010-5678-9012", "네이버", "13561", "성남시 분당구", "정자일로 95 네이버", "없음"))
                .when()
                .patch("api/v1/members/me/address/{addressId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AddressResponse.class);

        assertThat(addressResponse.addressName()).isEqualTo("네이버");
        assertThat(addressResponse.isDefaultedAddress()).isTrue();
    }

    @Test
    @DisplayName("기본값 수정 테스트1")
    public void 기본값수정1(){

        //로그인
        login();

        //주소생성
        createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-1234-5678", "광화문교보문고", "12345", "서울시 종로구 1", "교보생명빌딩 지하 1층", "없음", true));
        createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-5678-9012", "네이버", "13561", "성남시 분당구", "정자일로 95 네이버", "없음", true));
        Long addressId3 = createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-1234-5678", "코엑스", "06164", "서울시 강남구 삼성동", "영동대로 513", "없음", true));

        //기본값을 다시 기본값으로
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("user1"))
                .pathParam("addressId", addressId3)
                .when()
                .patch("api/v1/members/me/address/{addressId}/default")
                .then().log().all()
                .statusCode(200);

        //조회
        AddressResponse address3 = getDetailAddress(addressId3);

        assertThat(address3.isDefaultedAddress()).isTrue();

    }

    @Test
    @DisplayName("기본값 수정 테스트")
    public void 기본값수정2 (){

        //로그인
        login();

        //주소생성
        Long addressId1 = createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-1234-5678", "광화문교보문고", "12345", "서울시 종로구 1", "교보생명빌딩 지하 1층", "없음", true));
        createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-5678-9012", "네이버", "13561", "성남시 분당구", "정자일로 95 네이버", "없음", true));
        Long addressId3 = createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-1234-5678", "코엑스", "06164", "서울시 강남구 삼성동", "영동대로 513", "없음", true));

        //주소3 -> 주소1로 기본값 변경
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("user1"))
                .pathParam("addressId", addressId1)
                .when()
                .patch("api/v1/members/me/address/{addressId}/default")
                .then().log().all()
                .statusCode(200);

        AddressResponse address1 = getDetailAddress(addressId1);
        AddressResponse address3 = getDetailAddress(addressId3);

        assertThat(address1.isDefaultedAddress()).isTrue();
        assertThat(address3.isDefaultedAddress()).isFalse();

    }

    @Test
    @DisplayName("주소 삭제 테스트")
    public void 주소삭제 (){

        //로그인
        login();

        //주소생성
        Long addressId1 = createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-1234-5678", "광화문교보문고", "12345", "서울시 종로구 1", "교보생명빌딩 지하 1층", "없음", true));
        Long addressId2 = createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-5678-9012", "네이버", "13561", "성남시 분당구", "정자일로 95 네이버", "없음", true));
        Long addressId3 = createAddressId(new CreateAddressRequest(getMemberId("user1"), "유저1", "010-1234-5678", "코엑스", "06164", "서울시 강남구 삼성동", "영동대로 513", "없음", true));

        //address3 삭제
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("user1"))
                .pathParam("addressId", addressId3)
                .when()
                .delete("api/v1/members/me/address/{addressId}")
                .then().log().all()
                .statusCode(200);

        AddressResponse detailAddress1 = getDetailAddress(addressId1);
        AddressResponse detailAddress2 = getDetailAddress(addressId2);

        assertThat(detailAddress1.isDefaultedAddress()).isFalse();
        assertThat(detailAddress2.isDefaultedAddress()).isTrue();

    }



    private void login(){
        UserLoginResponse user1Login = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new UserLoginRequest("user1", "aAbB1234567890!"))
                .when()
                .post("api/v1/members/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(UserLoginResponse.class);
    }

    private Long createAddressId(CreateAddressRequest request){
        AddressResponse addressResponse = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("user1"))
                .body(request)
                .when()
                .post("api/v1/members/me/address")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AddressResponse.class);

        return addressResponse.id();
    }

    private AddressResponse getDetailAddress (Long addressId){
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken("user1"))
                .pathParam("addressId", addressId)
                .when()
                .get("api/v1/members/me/address/{addressId}")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(AddressResponse.class);
    }


    private void logPerfMetric (Response res){
        PerfMetrics metrics = PerfMetrics.from(res);
        System.out.println(metrics.format());
    }
}
