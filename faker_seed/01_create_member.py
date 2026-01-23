# 📄 faker_seed/generate_members.py
import random
import csv
from datetime import datetime, timedelta
from pathlib import Path

# === 설정 ===
NUM_BUYERS = 50000
NUM_SELLERS = 3

BASE_DIR = Path(__file__).resolve().parent.parent
OUTPUT_DIR = BASE_DIR / "data_seed"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

START_DATE = datetime(2025, 1, 1)
END_DATE = datetime(2025, 12, 31)
DAYS_RANGE = (END_DATE - START_DATE).days


# === 상태(memberStatus) 확률 ===
def get_status():
    roll = random.random()
    if roll < 0.80:
        return "ACTIVE"
    elif roll < 0.95:
        return "DORMANT"
    else:
        return "WITHDRAWN"


# === 랜덤 날짜 ===
def random_date():
    delta_days = random.randint(0, DAYS_RANGE)
    base = START_DATE + timedelta(days=delta_days)
    hour = random.randint(0, 23)
    minute = random.randint(0, 59)
    return base.replace(hour=hour, minute=minute, second=0)


# === 비밀번호 (bcrypt 고정 해시, 테스트용) ===
def random_password():
    return "$2a$10$3DfsP/D.6T9gMV1vAohWGO1Z/M0CMLyuyyogGVa2T27lVwrZlc0Tu"


# === 레벨 ===
def random_level():
    return random.choice(["BRONZE", "SILVER", "GOLD"])


# === total_spent_amount (int) ===
def random_total_spent_amount():
    return random.randint(0, 1_000_000)


# === 랜덤 포인트 ===
def random_point():
    return random.randint(0, 50000)


# === Member 데이터 생성 ===
def generate_members():
    members = []

    # 1️⃣ 판매자(Seller)
    for i in range(1, NUM_SELLERS + 1):
        created_at = START_DATE
        code = f"seller_{i:03d}"

        members.append({
            "id": i,
            "login_id": code,
            "password": random_password(),
            "name": f"Admin_{i:03d}",
            "nickname": f"AdminNick_{i:03d}",
            "email": f"{code}@market.com",
            "role": "SELLER",
            "member_status": "ACTIVE",
            "level": "BRONZE",
            "point": 0,
            "total_spent_amount": 0,
            "is_deleted": False,
            "deleted_at": "",
            "created_at": created_at.isoformat(),
            "updated_at": created_at.isoformat()
        })

    # 2️⃣ 일반 회원(Buyer)
    for i in range(NUM_SELLERS + 1, NUM_SELLERS + NUM_BUYERS + 1):
        code = f"member_{i:05d}"
        status = get_status()
        created_at = random_date()
        updated_at = created_at + timedelta(days=random.randint(0, 30))

        members.append({
            "id": i,
            "login_id": code,
            "password": random_password(),
            "name": f"name_{i:05d}",
            "nickname": f"nick_{i:05d}",
            "email": f"{code}@email.com",
            "role": "BUYER",
            "member_status": status,
            "level": random_level(),
            "point": random_point(),
            "total_spent_amount": random_total_spent_amount(),
            "is_deleted": False,
            "deleted_at": "",
            "created_at": created_at.isoformat(),
            "updated_at": updated_at.isoformat()
        })

    return members


# === CSV Export ===
def export_members_to_csv(members):
    file_path = OUTPUT_DIR / "members.csv"
    headers = [
        "id", "login_id", "password", "name", "nickname",
        "email", "role", "member_status", "level", "point",
        "total_spent_amount", "is_deleted", "deleted_at",
        "created_at", "updated_at"
    ]

    with open(file_path, "w", newline="", encoding="utf-8") as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=headers)
        writer.writeheader()
        writer.writerows(members)

    print(f"✅ members.csv 생성 완료 ({len(members):,}명) → {file_path}")


if __name__ == "__main__":
    data = generate_members()
    export_members_to_csv(data)
