package project.market.product;

import jakarta.persistence.*;
import project.market.BaseEntity;

@Entity
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cateName;

    @ManyToOne
    private ParentCategory parentCategory; //하위 카테고리 (ex. 긴소매,반소매 등)
}
