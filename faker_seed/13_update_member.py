# 📄 faker_seed/update_members_from_purchase_orders.py
import csv
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent
DATA_DIR = BASE_DIR / "data_seed"

MEMBERS_CSV = DATA_DIR / "members.csv"
PURCHASE_ORDERS_UPDATED_CSV = DATA_DIR / "purchase_orders_updated.csv"
MEMBERS_UPDATED_CSV = DATA_DIR / "members_updated.csv"

# 오타 케이스까지 제외 처리
EXCLUDED_STATUSES = {
    "CANCELED",
    "CANCEL_REQUESTED",
    "CANCEL_REQEUSTED",  # typo
    "CANCELD",           # typo
}

def parse_int(s, default=0):
    if s is None:
        return default
    s = str(s).strip()
    if s == "":
        return default
    return int(s)

def level_by_spent(total_spent: int) -> str:
    if total_spent >= 3_000_000:
        return "GOLD"
    if total_spent >= 1_000_000:
        return "SILVER"
    return "BRONZE"

def aggregate_orders():
    if not PURCHASE_ORDERS_UPDATED_CSV.exists():
        raise FileNotFoundError(f"purchase_orders_updated.csv를 찾을 수 없습니다: {PURCHASE_ORDERS_UPDATED_CSV}")

    spent_sum_by_member = {}
    point_sum_by_member = {}

    with open(PURCHASE_ORDERS_UPDATED_CSV, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            status = (row.get("order_status") or "").strip()
            if status in EXCLUDED_STATUSES:
                continue

            member_id = int(row["member_id"])
            pay_amount = parse_int(row.get("pay_amount"), 0)
            earn_point = parse_int(row.get("earn_point"), 0)

            spent_sum_by_member[member_id] = spent_sum_by_member.get(member_id, 0) + pay_amount
            point_sum_by_member[member_id] = point_sum_by_member.get(member_id, 0) + earn_point

    print(f"✅ 주문 집계 완료: spent 대상 회원 {len(spent_sum_by_member):,}명")
    return spent_sum_by_member, point_sum_by_member

def update_members(spent_sum_by_member, point_sum_by_member):
    if not MEMBERS_CSV.exists():
        raise FileNotFoundError(f"members.csv를 찾을 수 없습니다: {MEMBERS_CSV}")

    updated_rows = []
    with open(MEMBERS_CSV, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        headers = reader.fieldnames
        if headers is None:
            raise RuntimeError("members.csv 헤더를 읽을 수 없습니다.")

        required = {"id", "role", "point", "total_spent_amount", "level"}
        missing = required - set(headers)
        if missing:
            raise RuntimeError(f"members.csv에 필요한 컬럼이 없습니다: {missing}")

        for row in reader:
            member_id = int(row["id"])
            role = (row.get("role") or "").strip()

            # BUYER만 업데이트 (SELLER는 그대로 두는 게 안전)
            if role == "BUYER":
                total_spent = spent_sum_by_member.get(member_id, 0)
                total_point = point_sum_by_member.get(member_id, 0)

                row["total_spent_amount"] = str(total_spent)
                row["point"] = str(total_point)
                row["level"] = level_by_spent(total_spent)

            updated_rows.append(row)

    # members_updated.csv로 저장 (원본 보존)
    with open(MEMBERS_UPDATED_CSV, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=headers)
        writer.writeheader()
        writer.writerows(updated_rows)

    print(f"💾 members_updated.csv 저장 완료 → {MEMBERS_UPDATED_CSV}")

def main():
    spent_sum_by_member, point_sum_by_member = aggregate_orders()
    update_members(spent_sum_by_member, point_sum_by_member)

if __name__ == "__main__":
    main()
