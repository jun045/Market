package project.market.member.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import project.market.BaseEntity;
import project.market.address.entity.Address;
import project.market.member.enums.Level;
import project.market.member.enums.MemberStatus;
import project.market.member.enums.Role;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String loginId;

    @NotNull
    private String password;

    @NotNull
    private String name;

    private String nickname;

    @NotNull
    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Level level;

    private int point;

    private boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "member")
    private List<Address> addresses;

    @Builder
    public Member(Long id, String loginId, String password, String name, String nickname, String email, Role role, MemberStatus memberStatus, Level level, int point, boolean isDeleted, LocalDateTime deletedAt, List<Address> addresses) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.role = (role != null) ? role : Role.BUYER;
        this.memberStatus = (memberStatus != null) ? memberStatus:MemberStatus.DORMANT;
        this.level = (level != null) ? level:Level.BRONZE;
        this.point = point;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.addresses = addresses;
    }

    //회원정보 수정
    public void updateProfile (String name, String nickname, String email) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
    }

    //비밀번호 변경
    public void changePassword (String encodedPassword){
        if(encodedPassword.equals(password)){
            throw new RuntimeException("이전과 같은 비밀번호로는 변경할 수 없습니다.");
        }

        this.password = encodedPassword;
    }

    //회원탈퇴
    public void softDelete () {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
