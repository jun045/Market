# 📄 faker_seed/generate_reviews.py
import csv
import random
from pathlib import Path

random.seed(42)

BASE_DIR = Path(__file__).resolve().parent.parent
DATA_DIR = BASE_DIR / "data_seed"
DATA_DIR.mkdir(parents=True, exist_ok=True)

ORDER_ITEMS_CSV = DATA_DIR / "order_items.csv"
PURCHASE_ORDERS_UPDATED_CSV = DATA_DIR / "purchase_orders_updated.csv"
PRODUCT_VARIANTS_CSV = DATA_DIR / "product_variants.csv"

REVIEWS_CSV = DATA_DIR / "reviews.csv"

DELETE_RATIO = 0.10  # 10%


def load_order_to_member_map():
    """
    purchase_orders_updated.csv: order_id -> member_id
    """
    m = {}
    with open(PURCHASE_ORDERS_UPDATED_CSV, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            order_id = int(row["id"])
            member_id = int(row["member_id"])
            m[order_id] = member_id
    print(f"📥 purchase_orders_updated.csv 로드 완료: {len(m):,}개 (order_id -> member_id)")
    return m


def load_variant_to_product_map():
    """
    product_variants.csv: variant_id -> product_id
    """
    m = {}
    with open(PRODUCT_VARIANTS_CSV, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            variant_id = int(row["id"])
            product_id = int(row["product_id"])
            m[variant_id] = product_id
    print(f"📥 product_variants.csv 로드 완료: {len(m):,}개 (variant_id -> product_id)")
    return m


def parse_bool(s: str) -> bool:
    return str(s).strip().lower() == "true"


def generate_reviews():
    if not ORDER_ITEMS_CSV.exists():
        raise FileNotFoundError(f"order_items.csv를 찾을 수 없습니다: {ORDER_ITEMS_CSV}")
    if not PURCHASE_ORDERS_UPDATED_CSV.exists():
        raise FileNotFoundError(f"purchase_orders_updated.csv를 찾을 수 없습니다: {PURCHASE_ORDERS_UPDATED_CSV}")
    if not PRODUCT_VARIANTS_CSV.exists():
        raise FileNotFoundError(f"product_variants.csv를 찾을 수 없습니다: {PRODUCT_VARIANTS_CSV}")

    order_to_member = load_order_to_member_map()
    variant_to_product = load_variant_to_product_map()

    reviews = []
    review_id = 1

    with open(ORDER_ITEMS_CSV, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)

        for row in reader:
            is_reviewed = parse_bool(row.get("is_reviewed", "false"))
            if not is_reviewed:
                continue

            order_item_id = int(row["id"])
            purchase_order_id = int(row["purchase_order_id"])
            product_variant_id = int(row["product_variant_id"])
            created_at = (row.get("created_at") or "").strip()

            member_id = order_to_member.get(purchase_order_id)
            if member_id is None:
                raise RuntimeError(f"purchase_order_id={purchase_order_id}에 대한 member_id를 찾을 수 없습니다.")

            product_id = variant_to_product.get(product_variant_id)
            if product_id is None:
                raise RuntimeError(f"product_variant_id={product_variant_id}에 대한 product_id를 찾을 수 없습니다.")

            rating = random.randint(1, 5)
            content = f"review_content_{review_id}"

            is_deleted = random.random() < DELETE_RATIO
            deleted_at = created_at if is_deleted else ""

            reviews.append({
                "id": review_id,
                "member_id": member_id,
                "order_item_id": order_item_id,
                "product_id": product_id,
                "rating": rating,
                "content": content,
                "is_deleted": str(is_deleted).lower(),
                "created_at": created_at,
                "deleted_at": deleted_at,
                "updated_at": created_at,  # 규칙대로 created_at과 동일
            })

            review_id += 1

    print(f"✅ reviews 생성 완료: {len(reviews):,}개 (order_items.is_reviewed=true 기준)")
    return reviews


def export_reviews_to_csv(reviews):
    headers = [
        "id",
        "member_id",
        "order_item_id",
        "product_id",
        "rating",
        "content",
        "is_deleted",
        "created_at",
        "deleted_at",
        "updated_at",
    ]

    with open(REVIEWS_CSV, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=headers)
        writer.writeheader()
        writer.writerows(reviews)

    print(f"💾 reviews.csv 저장 완료 → {REVIEWS_CSV}")


if __name__ == "__main__":
    reviews = generate_reviews()
    export_reviews_to_csv(reviews)
