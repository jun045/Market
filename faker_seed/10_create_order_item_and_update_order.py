# 📄 faker_seed/generate_order_items_and_update_orders.py
import csv
import random
from pathlib import Path

random.seed(42)

BASE_DIR = Path(__file__).resolve().parent.parent
DATA_DIR = BASE_DIR / "data_seed"
DATA_DIR.mkdir(parents=True, exist_ok=True)

PURCHASE_ORDERS_CSV = DATA_DIR / "purchase_orders.csv"
PRODUCTS_CSV = DATA_DIR / "products.csv"
PRODUCT_VARIANTS_CSV = DATA_DIR / "product_variants.csv"

ORDER_ITEMS_CSV = DATA_DIR / "order_items.csv"
PURCHASE_ORDERS_UPDATED_CSV = DATA_DIR / "purchase_orders_updated.csv"

MIN_ITEMS_PER_ORDER = 1
MAX_ITEMS_PER_ORDER = 5

MIN_QTY = 1
MAX_QTY = 3

DELIVERED_STATUSES = {"DELIVERED", "DELEVERED"}  # 오타 케이스까지 허용


def load_products_list_price():
    """
    products.csv: id -> list_price(int)
    """
    m = {}
    with open(PRODUCTS_CSV, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            pid = int(row["id"])
            m[pid] = int(row["list_price"])
    print(f"📥 products.csv 로드 완료: {len(m):,}개")
    return m


def parse_int_nullable(s: str) -> int:
    if s is None:
        return 0
    s = str(s).strip()
    if s == "":
        return 0
    return int(s)


def load_product_variants():
    """
    product_variants.csv: id -> {product_id, extra_charge, discount_price}
    discount_price는 nullable이라 빈 문자열이면 0 처리(= 할인 없음)
    """
    m = {}
    with open(PRODUCT_VARIANTS_CSV, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            vid = int(row["id"])
            m[vid] = {
                "product_id": int(row["product_id"]),
                "extra_charge": int(row["extra_charge"]),
                "discount_price": parse_int_nullable(row.get("discount_price", "")),
            }
    print(f"📥 product_variants.csv 로드 완료: {len(m):,}개")
    return m


def load_purchase_orders():
    """
    purchase_orders.csv 전체를 row dict로 로드해서 업데이트 후 다시 저장하기 위함.
    """
    rows = []
    with open(PURCHASE_ORDERS_CSV, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        headers = reader.fieldnames
        for row in reader:
            rows.append(row)
    print(f"📥 purchase_orders.csv 로드 완료: {len(rows):,}개")
    return headers, rows


def generate_order_items_and_totals(order_rows, products_price_map, variants_map):
    """
    - order_item 생성
    - order_total_price / pay_amount 합계 산출
    """
    all_variant_ids = list(variants_map.keys())
    if not all_variant_ids:
        raise RuntimeError("product_variants.csv에 데이터가 없습니다.")

    order_items = []
    order_totals = {}  # purchase_order_id -> sum(total_price)

    item_id = 1

    for order in order_rows:
        purchase_order_id = int(order["id"])
        status = (order.get("order_status") or "").strip()
        created_at = order.get("created_at") or ""

        # 1) 주문당 item 개수
        num_items = random.randint(MIN_ITEMS_PER_ORDER, MAX_ITEMS_PER_ORDER)

        # 3) 주문 내 product_variant_id 중복 금지: sample로 뽑기
        # 혹시 variant 수가 부족하면 가능한 만큼만
        k = min(num_items, len(all_variant_ids))
        picked_variant_ids = random.sample(all_variant_ids, k=k)

        # 6) delivered 주문이면 item 중 50%만 reviewed=true
        reviewed_flags = [False] * k
        if status in DELIVERED_STATUSES:
            # 정확히 반만 true (홀수면 반올림 내림 + 50% 느낌으로 0/1 랜덤 보정)
            true_count = k // 2
            if k % 2 == 1 and random.random() < 0.5:
                true_count += 1
            idxs = list(range(k))
            random.shuffle(idxs)
            for idx in idxs[:true_count]:
                reviewed_flags[idx] = True

        total_sum = 0

        for idx, variant_id in enumerate(picked_variant_ids):
            v = variants_map[variant_id]
            product_id = v["product_id"]
            list_price = products_price_map.get(product_id)

            if list_price is None:
                # FK 정합성 깨진 데이터면 바로 알 수 있게 예외
                raise RuntimeError(f"products.csv에 product_id={product_id}가 없습니다. (variant_id={variant_id})")

            extra_charge = int(v["extra_charge"])
            discount_price = int(v["discount_price"])

            # 3) unit_price = list_price + extra_charge - discount_price
            unit_price = list_price + extra_charge - discount_price
            if unit_price < 0:
                # 할인액이 더 큰 이상치 방어
                unit_price = 0

            # 2) quantity 1~3
            quantity = random.randint(MIN_QTY, MAX_QTY)

            # 4) total_price = unit_price * quantity
            total_price = unit_price * quantity
            total_sum += total_price

            order_items.append({
                "id": item_id,
                "unit_price": unit_price,
                "total_price": total_price,
                "quantity": quantity,
                "purchase_order_id": purchase_order_id,
                "product_variant_id": variant_id,
                "created_at": created_at,
                "updated_at": created_at,
                "is_reviewed": str(reviewed_flags[idx]).lower(),
            })
            item_id += 1

        order_totals[purchase_order_id] = total_sum

    print(f"✅ order_items 생성 완료: {len(order_items):,}개")
    return order_items, order_totals


def export_order_items(order_items):
    headers = [
        "id",
        "unit_price",
        "total_price",
        "quantity",
        "purchase_order_id",
        "product_variant_id",
        "created_at",
        "updated_at",
        "is_reviewed",
    ]
    with open(ORDER_ITEMS_CSV, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=headers)
        writer.writeheader()
        writer.writerows(order_items)
    print(f"💾 order_items.csv 저장 완료 → {ORDER_ITEMS_CSV}")


def export_updated_purchase_orders(order_headers, order_rows, order_totals):
    """
    order_total_price, pay_amount, earn_point를 order_items 합계 기준으로 업데이트
    - used_point: 항상 0
    - earn_point: pay_amount의 1%
    """
    required_cols = {"order_total_price", "pay_amount", "earn_point", "used_point"}
    missing = required_cols - set(order_headers or [])
    if missing:
        raise RuntimeError(f"purchase_orders.csv에 컬럼이 없습니다: {missing}")

    for row in order_rows:
        oid = int(row["id"])
        total = order_totals.get(oid, 0)

        pay_amount = int(total)
        earn_point = int(pay_amount * 0.01)  # ✅ 1% 적립 (내림)

        row["order_total_price"] = str(total)
        row["pay_amount"] = str(pay_amount)
        row["used_point"] = "0"
        row["earn_point"] = str(earn_point)

    with open(PURCHASE_ORDERS_UPDATED_CSV, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=order_headers)
        writer.writeheader()
        writer.writerows(order_rows)

    print(f"💾 purchase_orders_updated.csv 저장 완료 → {PURCHASE_ORDERS_UPDATED_CSV}")



if __name__ == "__main__":
    products_price_map = load_products_list_price()
    variants_map = load_product_variants()
    order_headers, order_rows = load_purchase_orders()

    order_items, order_totals = generate_order_items_and_totals(
        order_rows=order_rows,
        products_price_map=products_price_map,
        variants_map=variants_map
    )

    export_order_items(order_items)
    export_updated_purchase_orders(order_headers, order_rows, order_totals)
