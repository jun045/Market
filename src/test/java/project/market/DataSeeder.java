package project.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.market.Brand.Brand;
import project.market.Brand.BrandRepository;
import project.market.Cate.Category;
import project.market.Cate.CategoryRepository;
import project.market.OrderItem.OrderItem;
import project.market.OrderItem.OrderItemRepository;
import project.market.ParentCategory.ParentCategory;
import project.market.ParentCategory.ParentCategoryRepository;
import project.market.ProductVariant.ProductVariant;
import project.market.ProductVariant.VariantRepository;
import project.market.PurchaseOrder.OrderStatus;
import project.market.PurchaseOrder.PurchaseOrderRepository;
import project.market.PurchaseOrder.entity.PurchaseOrder;
import project.market.address.AddressRepository;
import project.market.address.entity.Address;
import project.market.cart.entity.Cart;
import project.market.cart.entity.CartItem;
import project.market.cart.repository.CartItemRepository;
import project.market.cart.repository.CartRepository;
import project.market.member.Entity.Member;
import project.market.member.MemberRepository;
import project.market.member.enums.Level;
import project.market.member.enums.MemberStatus;
import project.market.member.enums.Role;
import project.market.product.*;

import java.util.List;

@Transactional
@Component
public class DataSeeder {

    @Autowired private MemberRepository memberRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ParentCategoryRepository parentCategoryRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private VariantRepository variantRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private PurchaseOrderRepository ordersRepository;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private PasswordEncoder passwordEncoder;


    Member user1;
    Member user2;
    Member admin;
    Address address1;
    Address address2;
    Category category;
    Brand brand;
    Brand brand2;
    Product product1;
    ProductVariant productVariant1;
    ProductVariant productVariant2;
    ProductVariant productVariant3;
    ProductVariant productVariant4;
    Cart cart1;
    CartItem cartItem1;
    CartItem cartItem2;
    CartItem cartItem3;
    CartItem cartItem4;
    PurchaseOrder order1;
    OrderItem orderItem1;
    OrderItem orderItem2;
    OrderItem orderItem3;
    OrderItem orderItem4;

    public Member createUser1 (){
        user1 = Member.builder()
                .loginId("userId1")
                .password("aAbB1234567890!")
                .name("유저1")
                .nickname("유저1닉네임")
                .email("user1@example.com")
                .role(Role.BUYER)
                .memberStatus(MemberStatus.ACTIVE)
                .level(Level.BRONZE)
                .point(1000000)
                .isDeleted(false)
                .deletedAt(null)
                .totalSpentAmount(0)
                .build();

        return memberRepository.save(user1);
    }

    public Member createUser2 (){
        user2 = Member.builder()
                .loginId("userId2")
                .password("aAbB1234567890!")
                .name("유저2")
                .nickname("유저2닉네임")
                .email("user2@example.com")
                .role(Role.BUYER)
                .memberStatus(MemberStatus.ACTIVE)
                .level(Level.BRONZE)
                .point(1000000)
                .isDeleted(false)
                .deletedAt(null)
                .totalSpentAmount(0)
                .build();

        return memberRepository.save(user2);
    }

    public Member createAdmin (){
        admin = Member.builder()
                .loginId("admin")
                .password("aAbB1234567890!")
                .name("관리자1")
                .nickname("관리자닉네임")
                .email("admin@example.com")
                .role(Role.SELLER)
                .memberStatus(MemberStatus.ACTIVE)
                .level(Level.BRONZE)
                .point(0)
                .isDeleted(false)
                .deletedAt(null)
                .totalSpentAmount(0)
                .build();

        return memberRepository.save(admin);
    }

    public Address createAddress1 (){
        address1 = new Address(
                "집",
                "홍길동",
                "0101234567",
                "01234",
                "서울시 강남구 강남대로",
                "1-1",
                "부재시문앞에",
                true,
                user1
        );

        return addressRepository.save(address1);
    }

    public Address createAddress2 (){
        address2 = new Address(
                "회사",
                "홍길동",
                "01012345678",
                "23456",
                "서울시 종로구 세종로",
                "교보빌딩 지하 교보문고",
                "",
                false,
                user1
        );

        return addressRepository.save(address2);
    }

    public ParentCategory createParentCategory (){
        ParentCategory parentCategory1 = new ParentCategory("상위카테고리1");

        return parentCategoryRepository.save(parentCategory1);
    }

    public Category createCategory (){
        category = new Category("카테고리1", createParentCategory());

        return categoryRepository.save(category);
    }

    public Brand createBrand(){
        brand = new Brand("애플", false);

        return brandRepository.save(brand);

    }

    public Brand createBrand2(){
        brand2 = new Brand("삼성", false);

        return brandRepository.save(brand2);
    }

    public Product createProduct1 (){
        product1 = Product.builder()
                .productName("아이폰16")
                .brand(brand)
                .description("아이폰 16 Pro")
                .thumbnail("thumb")
                .detailImage("상세이미지")
                .listPrice(1600000)
                .productStatus(ProductStatus.SALE)
                .isDeleted(false)
                .category(createCategory())
                .build();

        return productRepository.save(product1);
    }

    public ProductVariant createOptionVariants1 () {
        productVariant1 = ProductVariant.builder()
                .options("화이트, 256GB")
                .stock(100)
                .extraCharge(0)
                .product(product1)
                .build();

        return variantRepository.save(productVariant1);
    }

    public ProductVariant createOptionVariants2 () {
        productVariant2 = ProductVariant.builder()
                .options("화이트, 512GB")
                .stock(150)
                .extraCharge(0)
                .product(product1)
                .build();

        return variantRepository.save(productVariant2);
    }

    public ProductVariant createOptionVariants3 () {
        productVariant3 = ProductVariant.builder()
                .options("블랙, 256")
                .stock(200)
                .extraCharge(0)
                .product(product1)
                .build();

        return variantRepository.save(productVariant3);
    }

    public ProductVariant createOptionVariants4 () {
        productVariant4 = ProductVariant.builder()
                .options("블랙, 512GB")
                .stock(500)
                .extraCharge(0)
                .product(product1)
                .build();

        return variantRepository.save(productVariant4);
    }

    public Cart createCart1 (){
        cart1 = Cart.builder()
                .member(user1)
                .build();

        return cartRepository.save(cart1);
    }

    public List<CartItem> createCartItems (){
        cartItem1 = CartItem.builder()
                .cart(cart1)
                .productVariant(productVariant1)
                .product(product1)
                .quantity(1)
                .build();

        cartItem2 = CartItem.builder()
                .cart(cart1)
                .productVariant(productVariant2)
                .product(product1)
                .quantity(2)
                .build();

        cartItem3 = CartItem.builder()
                .cart(cart1)
                .productVariant(productVariant3)
                .product(product1)
                .quantity(3)
                .build();

        cartItem4 = CartItem.builder()
                .cart(cart1)
                .productVariant(productVariant4)
                .product(product1)
                .quantity(4)
                .build();

        return List.of(cartItemRepository.save(cartItem1),
                cartItemRepository.save(cartItem2),
                cartItemRepository.save(cartItem3),
                cartItemRepository.save(cartItem4));
    }

    public Cart createCartWithCartItems(){
        cart1 = createCart1();
        List<CartItem> cartItems = createCartItems();
        cartItems.forEach(cart1::addCart);

        return cartRepository.save(cart1);
    }

    public PurchaseOrder createOrder (){

        order1 = PurchaseOrder.builder()
                .orderStatus(OrderStatus.CREATED)
                .usedPoint(0)
                .earnPoint(0)
                .payAmount(100)
                .member(user1)
                .isDeleted(false)
                .build();

        order1.addOrderItem(productVariant1, 1);
        order1.addOrderItem(productVariant2, 2);
        order1.addOrderItem(productVariant3, 3);
        order1.addOrderItem(productVariant4, 4);

        return order1;

    }

    public PurchaseOrder createCompletedOrder (){

        PurchaseOrder completedOrder = PurchaseOrder.builder()
                .orderStatus(OrderStatus.DELIVERED)
                .merchantUid("order_test_merchant_uid")
                .usedPoint(0)
                .earnPoint(0)
                .payAmount(100)
                .member(user1)
                .isDeleted(false)
                .build();

        completedOrder.addOrderItem(productVariant1, 1);
        completedOrder.addOrderItem(productVariant2, 2);
        completedOrder.addOrderItem(productVariant3, 3);
        completedOrder.addOrderItem(productVariant4, 4);

        return ordersRepository.save(completedOrder);
    }



}
