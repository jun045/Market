# 📄 faker_seed/generate_purchase_orders.py
import csv
import random
from datetime import datetime, timedelta
from pathlib import Path

random.seed(42)

# === 경로 설정 ===
BASE_DIR = Path(__file__).resolve().parent.parent
DATA_DIR = BASE_DIR / "data_seed"
DATA_DIR.mkdir(parents=True, exist_ok=True)

MEMBERS_CSV = DATA_DIR / "members.csv"
PURCHASE_ORDERS_CSV = DATA_DIR / "purchase_orders.csv"

# === 설정값 ===
MIN_ORDERS_PER_MEMBER = 1
MAX_ORDERS_PER_MEMBER = 10

START_DATE = datetime(2025, 1, 1)
END_DATE = datetime(2025, 12, 31)
DAYS_RANGE = (END_DATE - START_DATE).days

# 상태 비율(정확히 맞춤)
STATUS_RATIOS = [
    ("PAID", 0.20),
    ("CREATED", 0.20),
    ("SHIPPED", 0.20),
    ("DELIVERED", 0.20),
    ("CANCEL_REQUESTED", 0.10),
    ("CANCELED", 0.10),
]

PAID_IMP_UID_PREFIX = "imp_"
MERCHANT_UID_PREFIX = "order_test_"


def random_datetime_2025():
    delta_days = random.randint(0, DAYS_RANGE)
    base = START_DATE + timedelta(days=delta_days)
    hour = random.randint(0, 23)
    minute = random.randint(0, 59)
    second = random.randint(0, 59)
    return base.replace(hour=hour, minute=minute, second=second)


def load_member_ids():
    if not MEMBERS_CSV.exists():
        raise FileNotFoundError(f"members.csv를 찾을 수 없습니다: {MEMBERS_CSV}")

    member_ids = []
    with open(MEMBERS_CSV, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            member_ids.append(int(row["id"]))

    print(f"📥 members.csv 로드 완료: {len(member_ids):,}명")
    return member_ids


def build_status_list(total_orders: int):
    """
    전체 주문 수 기준으로 정확히 비율 맞춰 status 리스트 생성 후 셔플
    """
    statuses = []
    allocated = 0

    for status, ratio in STATUS_RATIOS:
        cnt = int(total_orders * ratio)
        statuses.extend([status] * cnt)
        allocated += cnt

    # 반올림/내림으로 남는 개수는 앞에서부터 채움
    remaining = total_orders - allocated
    for i in range(remaining):
        statuses.append(STATUS_RATIOS[i % len(STATUS_RATIOS)][0])

    random.shuffle(statuses)
    return statuses


def generate_purchase_orders(member_ids):
    # 1) member별 주문 개수 먼저 결정
    member_orders_count = {mid: random.randint(MIN_ORDERS_PER_MEMBER, MAX_ORDERS_PER_MEMBER) for mid in member_ids}
    total_orders = sum(member_orders_count.values())

    statuses = build_status_list(total_orders)

    orders = []
    order_id = 1
    status_idx = 0

    # payment_id는 필요한 주문들만 1부터 순차
    payment_id_seq = 1

    for member_id in member_ids:
        for _ in range(member_orders_count[member_id]):
            order_status = statuses[status_idx]
            status_idx += 1

            # 공통: merchant_uid는 전 상태에 필수
            # 예시 형태: order_test_1767851656861 (숫자부는 timestamp처럼 보이게)
            merchant_uid = f"{MERCHANT_UID_PREFIX}{random.randint(1700000000000, 1799999999999)}"

            # 기본 금액/포인트는 0
            earn_point = 0
            order_total_price = 0
            pay_amount = 0
            used_point = 0

            # is_deleted는 전부 false
            is_deleted = "false"

            # 날짜 세팅: 요구대로 해당 상태에서 필요한 컬럼만 채우고, 모두 동일값
            base_dt = random_datetime_2025()
            iso_dt = base_dt.isoformat()

            created_at = iso_dt
            order_date = iso_dt
            updated_at = iso_dt

            paid_at = ""
            shipped_at = ""
            delivered_at = ""
            canceled_at = ""

            # paid_imp_uid / payment_id는 특정 상태에만
            paid_imp_uid = ""
            payment_id = ""

            if order_status in ("PAID", "SHIPPED", "DELIVERED"):
                paid_imp_uid = f"{PAID_IMP_UID_PREFIX}{random.randint(100000000000, 999999999999)}"
                payment_id = str(payment_id_seq)
                payment_id_seq += 1
                paid_at = iso_dt

            if order_status in ("SHIPPED", "DELIVERED"):
                shipped_at = iso_dt

            if order_status == "DELIVERED":
                delivered_at = iso_dt

            if order_status == "CANCELED":
                canceled_at = iso_dt

            # CREATED, CANCEL_REQUESTED는 created/order_date/updated만(이미 그렇게 됨)
            # CANCEL_REQUESTED는 canceled_at 없음 (요구사항대로)

            orders.append({
                "id": order_id,
                "member_id": member_id,
                "payment_id": payment_id,          # nullable: "" => null로 적재 가능
                "merchant_uid": merchant_uid,
                "paid_imp_uid": paid_imp_uid,      # nullable
                "earn_point": earn_point,
                "order_total_price": order_total_price,
                "pay_amount": pay_amount,
                "used_point": used_point,
                "order_status": order_status,
                "is_deleted": is_deleted,
                "created_at": created_at,
                "canceled_at": canceled_at,
                "delivered_at": delivered_at,
                "order_date": order_date,
                "paid_at": paid_at,
                "shipped_at": shipped_at,
                "updated_at": updated_at,
            })
            order_id += 1

    print(f"✅ purchase_order 생성 완료: {len(orders):,}개 (member {len(member_ids):,}명, member당 1~10개)")
    return orders


def export_purchase_orders_to_csv(orders):
    headers = [
        "id",
        "member_id",
        "payment_id",
        "merchant_uid",
        "paid_imp_uid",
        "earn_point",
        "order_total_price",
        "pay_amount",
        "used_point",
        "order_status",
        "is_deleted",
        "created_at",
        "canceled_at",
        "delivered_at",
        "order_date",
        "paid_at",
        "shipped_at",
        "updated_at",
    ]

    with open(PURCHASE_ORDERS_CSV, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=headers)
        writer.writeheader()
        writer.writerows(orders)

    print(f"💾 purchase_orders.csv 저장 완료 → {PURCHASE_ORDERS_CSV}")


if __name__ == "__main__":
    member_ids = load_member_ids()
    orders = generate_purchase_orders(member_ids)
    export_purchase_orders_to_csv(orders)
