package project.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.market.Brand.BrandRepository;
import project.market.Cate.CategoryRepository;
import project.market.ProductVariant.VariantRepository;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Level;
import project.market.member.enums.MemberStatus;
import project.market.member.enums.Role;
import project.market.product.ProductRepository;

@Component
public class DataSeeder {

    @Autowired private MemberRepository memberRepository;

    public Member createUser1 (){
        Member user1 = new Member("userId1",
                "aAbB1234567890!",
                "유저1",
                "닉네임1",
                "user1@example.com",
                Role.BUYER,
                MemberStatus.ACTIVE,
                Level.BRONZE,
                0,
                false,
                null);

        return memberRepository.save(user1);
    }

    public Member createUser2 (){
        Member user2 = new Member ("userId2",
                "aAbB1234567890!",
                "유저1",
                "닉네임1",
                "user2@example.com",
                Role.BUYER,
                MemberStatus.ACTIVE,
                Level.BRONZE,
                0,
                false,
                null);

        return memberRepository.save(user2);
    }

    public Member createAdmin (){
        Member admin = new Member ("admin",
                "aAbB1234567890!",
                "관리자1",
                "관리자닉네임1",
                "admin@example.com",
                Role.SELLER,
                MemberStatus.ACTIVE,
                Level.BRONZE,
                0,
                false,
                null);

        return memberRepository.save(admin);
    }
}
