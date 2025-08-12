package project.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class MemberTest extends AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private JwtProvider jwtProvider;

    @BeforeEach
    public void setUp(){

    }

    @Test
    @DisplayName("")
    public void 테스트 (){

    }

}