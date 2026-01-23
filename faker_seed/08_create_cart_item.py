import csv
import random
from datetime import datetime, timedelta
from pathlib import Path

# === 경로 설정 ===
BASE_DIR = Path(__file__).resolve().parent.parent
OUTPUT_DIR = BASE_DIR / "data_seed"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

# === 설정값 ===
MAX_ITEMS_PER_CART = 10       # 장바구니당 CartItem 개수 (1~10개)
MAX_DATE_OFFSET_DAYS = 30     # cart 생성 후 최대 30일 내
MAX_QUANTITY = 5              # 1~5개 수량

# === CSV 로더 ===
def load_carts():
    file_path = OUTPUT_DIR / "carts.csv"
    carts = []
    with open(file_path, newline="", encoding="utf-8") as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            carts.append({
                "id": int(row["id"]),
                "created_at": datetime.fromisoformat(row["created_at"])
            })
    print(f"📥 carts.csv 로드 완료: {len(carts)}개")
    return carts


def load_product_variants():
    file_path = OUTPUT_DIR / "product_variants.csv"
    variants = []
    with open(file_path, newline="", encoding="utf-8") as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            variants.append({
                "id": int(row["id"]),
                "product_id": int(row["product_id"])
            })
    print(f"📥 product_variants.csv 로드 완료: {len(variants)}개")
    return variants


# === 랜덤 날짜 생성 ===
def random_date_after(base_date):
    """cart.created_at 이후 ~ 30일 내 랜덤 날짜"""
    delta_days = random.randint(0, MAX_DATE_OFFSET_DAYS)
    random_time = timedelta(
        hours=random.randint(0, 23),
        minutes=random.randint(0, 59)
    )
    return base_date + timedelta(days=delta_days) + random_time


# === CartItem 생성 ===
def generate_cart_items(carts, variants):
    cart_items = []
    item_id = 1

    for cart in carts:
        num_items = random.randint(1, MAX_ITEMS_PER_CART)

        # ✅ cart_id 기준으로 이미 담긴 product_variant_id 추적
        used_variant_ids = set()

        # 중복 없이 뽑을 수 있는 최대 개수 보정
        available_variants = variants.copy()
        random.shuffle(available_variants)

        for variant in available_variants[:num_items]:
            product_variant_id = variant["id"]

            # 안전장치 (이론상 필요 없지만)
            if product_variant_id in used_variant_ids:
                continue

            used_variant_ids.add(product_variant_id)

            created = random_date_after(cart["created_at"])

            cart_items.append({
                "id": item_id,
                "cart_id": cart["id"],
                "product_id": variant["product_id"],
                "product_variant_id": product_variant_id,
                "quantity": random.randint(1, MAX_QUANTITY),
                "created_at": created.isoformat(),
                "updated_at": created.isoformat()
            })
            item_id += 1

    print(f"✅ CartItem 데이터 생성 완료: {len(cart_items)}개")
    return cart_items


# === CSV Export ===
def export_cart_items_to_csv(cart_items):
    file_path = OUTPUT_DIR / "cart_items.csv"
    headers = [
        "id",
        "cart_id",
        "product_id",
        "product_variant_id",
        "quantity",
        "created_at",
        "updated_at"
    ]

    with open(file_path, "w", newline="", encoding="utf-8") as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=headers)
        writer.writeheader()
        writer.writerows(cart_items)

    print(f"💾 cart_items.csv 저장 완료 → {file_path}")


# === 실행 ===
if __name__ == "__main__":
    carts = load_carts()
    variants = load_product_variants()
    cart_items = generate_cart_items(carts, variants)
    export_cart_items_to_csv(cart_items)
