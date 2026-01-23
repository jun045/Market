# 📄 faker_seed/generate_addresses.py
import csv
import random
from datetime import datetime, timedelta
from pathlib import Path
from faker import Faker

fake = Faker("ko_KR")
random.seed(42)

# === 설정 ===
BASE_DIR = Path(__file__).resolve().parent.parent
OUTPUT_DIR = BASE_DIR / "data_seed"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

MEMBER_CSV = OUTPUT_DIR / "members.csv"

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


def random_phone():
    mid = random.randint(1000, 9999)
    end = random.randint(1000, 9999)
    return f"010-{mid}-{end}"


def random_postal_code():
    return f"{random.randint(10000, 99999)}"


def random_request():
    messages = [
        "문 앞에 놓아주세요.",
        "벨 누르지 말고 문 앞에 두세요.",
        "부재 시 경비실에 맡겨주세요.",
        "배송 전 연락 부탁드립니다.",
        "조심히 다뤄주세요."
    ]
    return random.choice(messages) if random.random() < 0.3 else ""


def random_address_name():
    roll = random.random()
    if roll < 0.5:
        return "집"
    elif roll < 0.8:
        return "회사"
    else:
        return "기타"


def random_address_text():
    addr = fake.address().split("\n")[0][:30]
    detail = f"{random.randint(101, 109)}동 {random.randint(101, 1905)}호"
    return addr, detail


def iter_members(member_csv_path: Path):
    """
    members.csv에서 id, name만 스트리밍으로 읽음 (pandas 불필요)
    """
    with open(member_csv_path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            yield int(row["id"]), row["name"]


# === Address 데이터 생성 ===
def generate_addresses():
    if not MEMBER_CSV.exists():
        raise FileNotFoundError(f"members.csv를 찾을 수 없습니다: {MEMBER_CSV}")

    addresses = []
    address_id = 1
    member_count = 0

    for member_id, recipient_name in iter_members(MEMBER_CSV):
        member_count += 1

        # ✅ 모든 회원: 주소 1~3개 랜덤 (k6 전용 분기 제거)
        num_addresses = random.choices([1, 2, 3], weights=[0.6, 0.3, 0.1])[0]

        default_set = False

        for _ in range(num_addresses):
            created_at = random_date()
            updated_at = created_at + timedelta(days=random.randint(0, 15))
            addr_name = random_address_name()
            addr, detail = random_address_text()
            request = random_request()
            postal = random_postal_code()
            phone = random_phone()

            # 회원당 첫 주소는 기본 배송지로
            is_default = not default_set
            default_set = True

            addresses.append({
                "id": address_id,
                "member_id": member_id,
                "address_name": addr_name,
                "recipient_name": recipient_name,
                "recipient_phone": phone,
                "postal_code": postal,
                "address": addr,
                "detail_address": detail,
                "request": request,
                "is_defaulted_address": is_default,
                "created_at": created_at.isoformat(),
                "updated_at": updated_at.isoformat()
            })
            address_id += 1

    print(f"✅ 생성 완료: {len(addresses):,}개의 주소 (회원 {member_count:,}명 기준)")
    return addresses


# === CSV Export ===
def export_addresses_to_csv(addresses):
    file_path = OUTPUT_DIR / "addresses.csv"
    headers = [
        "id", "member_id", "address_name", "recipient_name", "recipient_phone",
        "postal_code", "address", "detail_address", "request",
        "is_defaulted_address", "created_at", "updated_at"
    ]
    with open(file_path, "w", newline="", encoding="utf-8") as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=headers)
        writer.writeheader()
        writer.writerows(addresses)

    print(f"💾 addresses.csv 저장 완료 → {file_path}")


if __name__ == "__main__":
    data = generate_addresses()
    export_addresses_to_csv(data)
