package project.market.Brand;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    String brandName;

    //String brandDescription;

    private boolean isDeleted = false;

    @Builder
    public Brand(String brandName,
                 boolean isDeleted) {
        this.brandName = brandName;
        this.isDeleted = isDeleted;
    }

    //소프트 딜리트
    public void deletedBrand(){
        this.isDeleted = true;
    }
}
