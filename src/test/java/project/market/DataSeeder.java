package project.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.market.Brand.Brand;
import project.market.Brand.BrandRepository;
import project.market.cart.repository.CartItemRepository;
import project.market.cart.repository.CartRepository;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Level;
import project.market.member.enums.MemberStatus;
import project.market.member.enums.Role;
import project.market.product.*;

import java.util.List;

@Component
public class DataSeeder {

    @Autowired private MemberRepository memberRepository;
    @Autowired private ParentCategoryRepository parentCategoryRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OptionVariantRepository optionVariantRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private CartRepository cartRepository;


    public Member createMember (){
        Member user1 = new Member(
                "userId",
                "aAbB1234567890!",
                "유저1",
                "닉네임1",
                "email@exampl.com",
                Role.BUYER,
                MemberStatus.ACTIVE,
                Level.BRONZE,
                0,
                false,
                null);

        return memberRepository.save(user1);
    }

    public Member createAdmin (){
        Member admin1 = new Member(
                "adminId",
                "aAbB1234567890!",
                "관리자1",
                "관리자닉네임1",
                "adminemail@exampl.com",
                Role.SELLER,
                MemberStatus.ACTIVE,
                Level.BRONZE,
                0,
                false,
                null);

        return memberRepository.save(admin1);
    }

    public ParentCategory createParentCategory (){
        ParentCategory parentCategory1 = new ParentCategory("상위카테고리1");

        return parentCategoryRepository.save(parentCategory1);
    }

    public Category createCategory (){
        Category category1 = new Category("카테고리1", createParentCategory());

        return categoryRepository.save(category1);
    }

    public Brand createBrand(){
        Brand brand = new Brand("애플", false);

        return brandRepository.save(brand);

    }
//
//    public Product createProduct1 (){
//        Product product1 = new Product("아이폰 16", createBrand(), "아이폰 16 Pro", "thumb", "상세이미지", 1600000, ProductStatus.SALE, false, createCategory(),
//                List.of(optionVariant1(), optionVariant2(), optionVariant3(), optionVariant4()));
//
//        return productRepository.save(product1);
//    }

//    public OptionVariant optionVariant1 (Product product){
//        OptionVariant optionVariant1 = new OptionVariant("화이트, 256GB", 10, 0, 1600000, createProduct1());
//
//        product.addVariant(optionVariant1);
//        return optionVariantRepository.save(optionVariant1);
//    }
//
//    public OptionVariant optionVariant2 (){
//        OptionVariant optionVariant2 = new OptionVariant("화이트, 512GB", 15, 100000, 1700000, createProduct1());
//        return optionVariantRepository.save(optionVariant2);
//    }
//
//    public OptionVariant optionVariant3 (){
//        OptionVariant optionVariant3 = new OptionVariant("블랙, 256GB", 20, 0, 1600000, createProduct1());
//        return optionVariantRepository.save(optionVariant3);
//    }
//
//    public OptionVariant optionVariant4 (){
//        OptionVariant optionVariant4 = new OptionVariant("블랙, 512GB", 5, 100000, 1700000, createProduct1());
//        return optionVariantRepository.save(optionVariant4);
//    }

//    public OptionVariant createVariant (String summary, Integer stock, Integer extraCharge){
//        OptionVariant optionVariant = new OptionVariant(summary, stock, extraCharge)
//    }



}
