# 📄 faker_seed/generate_brands.py
import csv
import random
from pathlib import Path
from datetime import datetime, timedelta

# === 설정 ===
BASE_DIR = Path(__file__).resolve().parent.parent

OUTPUT_DIR = BASE_DIR / "data_seed"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

TOTAL_BRANDS = 500
DELETE_RATIO = 0.05  # 5%

START_DATE = datetime(2025, 1, 1)
END_DATE = datetime(2025, 12, 31)
DAYS_RANGE = (END_DATE - START_DATE).days


# === 랜덤 날짜 ===
def random_date():
    delta_days = random.randint(0, DAYS_RANGE)
    return START_DATE + timedelta(days=delta_days)


# === Brand 데이터 생성 ===
def generate_brands():
    brands = []

    for i in range(1, TOTAL_BRANDS + 1):
        brand_id = i
        brand_name = f"brand_{brand_id:05d}"

        # 5% 확률로 soft delete
        is_deleted = random.random() < DELETE_RATIO

        created_at = random_date()
        updated_at = created_at  # ✅ 동일 날짜로 채움

        brands.append({
            "id": brand_id,
            "brand_name": brand_name,
            "is_deleted": str(is_deleted).lower(),
            "created_at": created_at.isoformat(),
            "updated_at": updated_at.isoformat()
        })

    print(f"✅ 브랜드 데이터 생성 완료: {len(brands)}개 (삭제 상태 {DELETE_RATIO*100:.0f}% 비율)")
    return brands


# === CSV Export ===
def export_brands_to_csv(brands):
    file_path = OUTPUT_DIR / "brands.csv"
    headers = ["id", "brand_name", "is_deleted", "created_at", "updated_at"]

    with open(file_path, "w", newline="", encoding="utf-8") as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=headers)
        writer.writeheader()
        writer.writerows(brands)

    print(f"💾 brands.csv 저장 완료 → {file_path}")


if __name__ == "__main__":
    brands = generate_brands()
    export_brands_to_csv(brands)
