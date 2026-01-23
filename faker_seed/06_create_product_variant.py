# 📄 faker_seed/generate_product_variants.py
import csv
import random
import json
from pathlib import Path
from datetime import datetime
from faker import Faker

fake = Faker("ko_KR")

# === 경로 설정 ===
BASE_DIR = Path(__file__).resolve().parent.parent
DATA_DIR = BASE_DIR / "data_seed"
DATA_DIR.mkdir(parents=True, exist_ok=True)

PRODUCTS_CSV = DATA_DIR / "products.csv"
CATEGORIES_CSV = DATA_DIR / "categories.csv"
PRODUCT_VARIANTS_CSV = DATA_DIR / "product_variants.csv"

MIN_VARIANTS = 2
MAX_VARIANTS = 5

# === 카테고리별 옵션 템플릿 ===
CATEGORY_OPTION_TEMPLATES = {
    "남성의류": [["Black", "White", "Gray", "Navy"], ["S", "M", "L", "XL"]],
    "여성의류": [["Beige", "Ivory", "Pink", "Black"], ["S", "M", "L"]],
    "아동복": [["Red", "Blue", "Yellow"], ["100", "110", "120", "130"]],
    "신발": [["White", "Black"], ["230", "240", "250", "260", "270"]],
    "가방": [["Brown", "Black", "Gray"], ["Small", "Medium", "Large"]],
    "액세서리": [["Gold", "Silver"], ["단품", "세트"]],
    "스마트폰": [["Black", "Silver", "Blue"], ["128GB", "256GB", "512GB"]],
    "노트북": [["Black", "Silver", "Blue"], ["256GB", "512GB", "1TB"]],
    "가전제품": [["White", "Gray", "Black"], ["기본형", "프리미엄"]],
    "웨어러블": [["Black", "Pink", "Gray"], ["S", "M", "L"]],
    "스킨케어": [["기본형", "리필"], ["30ml", "50ml", "100ml"]],
    "메이크업": [["Light", "Medium", "Dark"], ["30ml", "50ml", "100ml"]],
    "향수": [["남성용", "여성용"], ["30ml", "50ml", "100ml"]],
    "신선식품": [["냉장", "냉동"], ["1팩", "2팩", "3팩"]],
    "간편식": [["냉장", "냉동"], ["1인분", "2인분", "4인분"]],
    "음료": [["250ml", "500ml", "1L"], ["플레인", "제로"]],
    "청소용품": [["화이트", "블루"], ["1개입", "2개입"]],
    "주방용품": [["스테인리스", "플라스틱"], ["1개입", "3개입"]],
    "욕실용품": [["화이트", "그레이"], ["1개입", "2개입"]],
    "거실가구": [["Oak", "Walnut"], ["Small", "Medium", "Large"]],
    "조명": [["전구색", "주광색"], ["단일형", "세트"]],
    "수납정리": [["화이트", "우드"], ["1단", "2단", "3단"]],
    "운동화": [["Black", "White", "Navy"], ["250", "260", "270", "280"]],
    "캠핑용품": [["실버", "블랙"], ["1인용", "2인용", "4인용"]],
    "자전거": [["Gray", "Blue"], ["20인치", "24인치", "26인치"]],
    "헬스기구": [["Gray", "Black"], ["Light", "Medium", "Heavy"]],
    "소설": [["페이퍼백", "양장본"], ["단권", "세트"]],
    "음악/악기": [["기타", "피아노", "드럼"], ["Beginner", "Pro"]],
    "문구용품": [["Blue", "Red"], ["1개입", "5개입", "10개입"]],
    "사료": [["닭고기", "연어"], ["1kg", "2kg", "5kg"],],
    "장난감": [["플라스틱", "고무"], ["Small", "Medium", "Large"]],
    "미용용품": [["핑크", "화이트"], ["1개입", "3개입"]],
}

# 옵션 key는 팀 코드처럼 고정(색상/사이즈)으로 맞춤
OPTION_KEY_1 = "색상/종류"
OPTION_KEY_2 = "사이즈"


# === CSV 로드 ===
def load_categories():
    categories = {}
    with open(CATEGORIES_CSV, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            categories[int(row["id"])] = row["cate_name"]
    return categories


def load_product_data():
    """
    products.csv가 최신 포맷(list_price, created_at)을 사용한다는 전제.
    """
    product_map = {}
    with open(PRODUCTS_CSV, newline="", encoding="utf-8") as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            product_id = int(row["id"])
            list_price = int(row["list_price"])  # ✅ base_price -> list_price (int)
            category_id = int(row["category_id"])
            created_at = row.get("created_at") or datetime.now().isoformat()

            product_map[product_id] = {
                "list_price": list_price,
                "category_id": category_id,
                "created_at": created_at,
            }

    print(f"📦 Product 데이터 로드 완료: {len(product_map)}건")
    return product_map


# === options(JSON 문자열) 생성 ===
def build_options_json(attr1: str, attr2: str) -> str:
    options_map = {
        OPTION_KEY_1: [attr1],
        OPTION_KEY_2: [attr2],
    }
    # ensure_ascii=False: 한글이 \uXXXX로 안 깨지고 그대로 들어감
    # separators: 공백 제거해서 응답/비교시 흔들림 줄임
    return json.dumps(options_map, ensure_ascii=False, separators=(",", ":"))


# === ProductVariant 데이터 생성 ===
def generate_product_variants(product_map, categories):
    variants = []
    variant_id = 1

    for product_id, product_data in product_map.items():
        category_id = product_data["category_id"]
        category_name = categories.get(category_id, "기타")
        templates = CATEGORY_OPTION_TEMPLATES.get(category_name, [["Default"], ["OneSize"]])

        num_variants = random.randint(MIN_VARIANTS, MAX_VARIANTS)

        for _ in range(num_variants):
            attr1 = random.choice(templates[0])
            attr2 = random.choice(templates[1])

            # (기존 option_summary는 제거, 대신 options(JSON 문자열)로)
            options_json = build_options_json(attr1, attr2)

            stock = random.randint(0, 100)

            # extra_charge: int (list_price의 20% 범위 내에서 100원 단위)
            max_charge = int(product_data["list_price"] * 0.2)
            max_charge_rounded = (max_charge // 100) * 100
            extra_charge = random.randrange(0, max_charge_rounded + 100, 100)

            # discount_price: Long(nullable) -> CSV에서는 빈 문자열로
            # 15% 확률로 할인값 생성(원하면 확률/규칙 바꿔도 됨)
            discount_price = ""
            if random.random() < 0.15:
                # 0 ~ extra_charge 범위 내(또는 0~list_price의 10% 등)로 "대충" 넣기
                discount_price_val = random.randrange(0, min(extra_charge, 50_000) + 100, 100)
                discount_price = str(int(discount_price_val))

            created_at = product_data["created_at"]
            updated_at = created_at
            is_deleted = False

            # version (@Version): 보통 0부터 시작, null이면 JPA가 불편해질 때가 많음
            # @NotNull 없어도 "초기값 0"이 가장 안전
            version = 0

            variants.append({
                "id": variant_id,
                "product_id": product_id,
                "options": options_json,
                "stock": stock,
                "extra_charge": int(extra_charge),
                "discount_price": discount_price,  # ""이면 null로 적재하기 쉬움
                "version": version,
                "is_deleted": str(is_deleted).lower(),
                "created_at": created_at,
                "updated_at": updated_at,
            })
            variant_id += 1

    print(f"✅ ProductVariant 데이터 생성 완료: {len(variants):,}개")
    return variants


# === CSV Export ===
def export_product_variants_to_csv(variants):
    headers = [
        "id",
        "product_id",
        "options",
        "stock",
        "extra_charge",
        "discount_price",
        "version",
        "is_deleted",
        "created_at",
        "updated_at",
    ]
    with open(PRODUCT_VARIANTS_CSV, "w", newline="", encoding="utf-8") as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=headers)
        writer.writeheader()
        writer.writerows(variants)

    print(f"💾 product_variants.csv 저장 완료 → {PRODUCT_VARIANTS_CSV}")


if __name__ == "__main__":
    categories = load_categories()
    product_map = load_product_data()
    product_variants = generate_product_variants(product_map, categories)
    export_product_variants_to_csv(product_variants)
