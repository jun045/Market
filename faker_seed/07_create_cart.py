# 📄 faker_seed/generate_carts.py
import csv
import random
from datetime import datetime, timedelta
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent
OUTPUT_DIR = BASE_DIR / "data_seed"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

START_DATE = datetime(2025, 1, 1)
END_DATE = datetime(2025, 12, 31)
DAYS_RANGE = (END_DATE - START_DATE).days

MEMBER_START = 4
MEMBER_END = 50003
CART_RATIO = 0.7  # 70% 회원에게 Cart 생성

def random_date():
    delta_days = random.randint(0, DAYS_RANGE)
    base = START_DATE + timedelta(days=delta_days)
    hour = random.randint(0, 23)
    minute = random.randint(0, 59)
    return base.replace(hour=hour, minute=minute, second=0)

def generate_carts():
    all_members = list(range(MEMBER_START, MEMBER_END + 1))
    selected_members = random.sample(all_members, int(len(all_members) * CART_RATIO))

    carts = []
    for i, member_id in enumerate(sorted(selected_members), start=1):
        created = random_date()
        carts.append({
            "id": i,
            "member_id": member_id,
            "created_at": created.isoformat(),
            "updated_at": created.isoformat()
        })
    print(f"✅ Cart 데이터 생성 완료: {len(carts)}개")
    return carts

def export_carts_to_csv(carts):
    file_path = OUTPUT_DIR / "carts.csv"
    headers = ["id", "member_id", "created_at", "updated_at"]

    with open(file_path, "w", newline="", encoding="utf-8") as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=headers)
        writer.writeheader()
        writer.writerows(carts)
    print(f"💾 carts.csv 저장 완료 → {file_path}")

if __name__ == "__main__":
    carts = generate_carts()
    export_carts_to_csv(carts)
