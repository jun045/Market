package project.market.Cate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.market.ParentCategory.ParentCategory;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cateName;

    @ManyToOne
    private ParentCategory parentCategory; //하위 카테고리 (ex. 긴소매,반소매 등)

    @Builder
    public Category(String cateName, ParentCategory parentCategory) {
        this.cateName = cateName;
        this.parentCategory = parentCategory;
    }
}
