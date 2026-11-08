package project.market.member;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import project.market.member.Entity.QMember;
import project.market.member.dto.UserListResponse;
import project.market.member.enums.Level;
import project.market.member.enums.MemberStatus;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QMember qMember = QMember.member;

    public Page<UserListResponse> getAllUsers (Pageable pageable,
                                               String loginId,
                                               String email,
                                               Level level,
                                               MemberStatus memberStatus){

        List<UserListResponse> content = jpaQueryFactory
                .select(Projections.constructor(UserListResponse.class,
                        qMember.id,
                        qMember.loginId,
                        qMember.email,
                        qMember.level,
                        qMember.point,
                        qMember.memberStatus,
                        qMember.createdAt))
                .from(qMember)
                .where(loginIdContains(loginId),
                        emailContains(email),
                        levelEq(level),
                        memberStatusEq(memberStatus)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qMember.id.desc())
                .fetch();

        Long total = jpaQueryFactory
                .select(qMember.count())
                .from(qMember)
                .where(loginIdContains(loginId),
                        emailContains(email),
                        levelEq(level),
                        memberStatusEq(memberStatus)
                ).fetchOne();

        long totalElement = total != null ? total : 0L;

        return new PageImpl<>(content, pageable, totalElement);
    }

    BooleanExpression levelEq (Level level){
        return level == null ? null : qMember.level.eq(level);
    }

    BooleanExpression memberStatusEq (MemberStatus memberStatus){
        return  memberStatus == null ? null : qMember.memberStatus.eq(memberStatus);
    }

    // login_id 부분 검색 (idx_login_id_trgm 인덱스 사용)
    BooleanExpression loginIdContains (String loginId){
        return ilikeContains(qMember.loginId, loginId);
    }

    // email 부분 검색 (idx_email_trgm 인덱스 사용)
    BooleanExpression emailContains (String email){
        return ilikeContains(qMember.email, email);
    }

    // raw ILIKE 로 GIN trigram 인덱스를 사용하는 부분검색 조건 생성
    private BooleanExpression ilikeContains (StringPath column, String value){
        if (value == null || value.isBlank()) {
            return null;
        }
        // LIKE 와일드카드(%, _)와 이스케이프 문자(!)를 리터럴로 처리하기 위해 이스케이프
        String escaped = value.replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return Expressions.booleanTemplate(
                "{0} ilike {1} escape '!'", column, "%" + escaped + "%");
    }
}
