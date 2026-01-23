# 📄 faker_seed/generate_products.py
import csv
import random
from datetime import datetime, timedelta
from pathlib import Path

# === 설정 ===
BASE_DIR = Path(__file__).resolve().parent.parent
OUTPUT_DIR = BASE_DIR / "data_seed"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

TOTAL_PRODUCTS = 20000
BRAND_RANGE = (1, 500)
CATEGORY_FILE = OUTPUT_DIR / "categories.csv"
BUCKET_NAME = "market-bucket"
REGION = "ap-northeast-2"

START_DATE = datetime(2025, 1, 1)
END_DATE = datetime(2025, 12, 31)
DAYS_RANGE = (END_DATE - START_DATE).days


# === 랜덤 날짜 ===
def random_date():
    delta_days = random.randint(0, DAYS_RANGE)
    base = START_DATE + timedelta(days=delta_days)
    hour = random.randint(0, 23)
    minute = random.randint(0, 59)
    return base.replace(hour=hour, minute=minute, second=0)


# === 카테고리 로드 ===
def load_categories():
    categories = {}
    if not CATEGORY_FILE.exists():
        raise FileNotFoundError(f"❌ categories.csv 파일을 찾을 수 없습니다: {CATEGORY_FILE}")

    with open(CATEGORY_FILE, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            categories[int(row["id"])] = row["cate_name"]

    print(f"📂 카테고리 로드 완료: {len(categories)}개")
    return categories


# === Product 데이터 생성 ===
def generate_products(categories):
    products = []

    for i in range(1, TOTAL_PRODUCTS + 1):
        product_id = i
        brand_id = random.randint(*BRAND_RANGE)
        category_id = random.choice(list(categories.keys()))
        cate_name = categories[category_id].replace("/", "_")

        product_name = f"{cate_name}_product_{product_id:05d}"
        description = f"description for {product_name}"

        thumbnail = (
            f"https://{BUCKET_NAME}.s3.{REGION}.amazonaws.com/"
            f"products/{product_id:05d}/thumbnail.jpg"
        )

        # ✅ detail_image: thumbnail → detail 치환
        detail_image = thumbnail.replace("thumbnail", "detail")

        list_price = random.randrange(5_000, 500_001, 100)
        like_count = random.randint(0, 10_000)
        product_status = "SALE" if random.random() < 0.9 else "SOLD_OUT"

        products.append({
            "id": product_id,
            "brand_id": brand_id,
            "category_id": category_id,
            "product_name": product_name,
            "description": description,
            "thumbnail": thumbnail,
            "detail_image": detail_image,
            "list_price": list_price,
            "like_count": like_count,
            "product_status": product_status,
            "is_deleted": "false",
            "created_at": START_DATE.isoformat(),
            "updated_at": START_DATE.isoformat()
        })

    print(f"✅ Product 데이터 생성 완료: {len(products)}개")
    return products


# === CSV Export ===
def export_products_to_csv(products):
    product_file = OUTPUT_DIR / "products.csv"
    headers = [
        "id",
        "brand_id",
        "category_id",
        "product_name",
        "description",
        "thumbnail",
        "detail_image",
        "list_price",
        "like_count",
        "product_status",
        "is_deleted",
        "created_at",
        "updated_at"
    ]

    with open(product_file, "w", newline="", encoding="utf-8") as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=headers)
        writer.writeheader()
        writer.writerows(products)

    print(f"💾 products.csv 저장 완료 → {product_file}")


if __name__ == "__main__":
    categories = load_categories()
    products = generate_products(categories)
    export_products_to_csv(products)
