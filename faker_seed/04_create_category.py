# 📄 faker_seed/generate_categories.py
import csv
from pathlib import Path

# === 출력 경로 설정 ===

# 현재 파일 위치 기준으로 상위 폴더(= 프로젝트 루트) 계산
BASE_DIR = Path(__file__).resolve().parent.parent

# data_seed/static 경로로 지정
OUTPUT_DIR = BASE_DIR / "data_seed" / "static"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

# === parent_category 데이터 ===
parent_categories = [
    {"id": 1, "parent_cate_name": "패션의류"},
    {"id": 2, "parent_cate_name": "패션잡화"},
    {"id": 3, "parent_cate_name": "디지털/가전"},
    {"id": 4, "parent_cate_name": "뷰티"},
    {"id": 5, "parent_cate_name": "식품"},
    {"id": 6, "parent_cate_name": "생활/주방"},
    {"id": 7, "parent_cate_name": "가구/인테리어"},
    {"id": 8, "parent_cate_name": "스포츠/레저"},
    {"id": 9, "parent_cate_name": "도서/취미"},
    {"id": 10, "parent_cate_name": "반려동물"},
]

# === category 데이터 (parent_category별 연결) ===
categories = [
    # 패션의류
    (1, ["남성의류", "여성의류", "아동복"]),
    # 패션잡화
    (2, ["신발", "가방", "액세서리"]),
    # 디지털/가전
    (3, ["스마트폰", "노트북", "가전제품", "웨어러블"]),
    # 뷰티
    (4, ["스킨케어", "메이크업", "향수"]),
    # 식품
    (5, ["신선식품", "간편식", "음료"]),
    # 생활/주방
    (6, ["청소용품", "주방용품", "욕실용품"]),
    # 가구/인테리어
    (7, ["거실가구", "조명", "수납정리"]),
    # 스포츠/레저
    (8, ["운동화", "캠핑용품", "자전거", "헬스기구"]),
    # 도서/취미
    (9, ["소설", "음악/악기", "문구용품"]),
    # 반려동물
    (10, ["사료", "장난감", "미용용품"]),
]

# === CSV 파일 생성 함수 ===
def export_parent_categories():
    file_path = OUTPUT_DIR / "parent_categories.csv"
    headers = ["id", "parent_cate_name"]

    with open(file_path, "w", newline="", encoding="utf-8") as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=headers)
        writer.writeheader()
        writer.writerows(parent_categories)

    print(f"✅ parent_categories.csv 생성 완료 ({len(parent_categories)}개) → {file_path}")


def export_categories():
    file_path = OUTPUT_DIR / "categories.csv"
    headers = ["id", "parent_category_id", "cate_name"]

    rows = []
    cate_id = 1
    for parent_id, cate_names in categories:
        for name in cate_names:
            rows.append({
                "id": cate_id,
                "parent_category_id": parent_id,
                "cate_name": name
            })
            cate_id += 1

    with open(file_path, "w", newline="", encoding="utf-8") as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=headers)
        writer.writeheader()
        writer.writerows(rows)

    print(f"✅ categories.csv 생성 완료 ({len(rows)}개) → {file_path}")


if __name__ == "__main__":
    export_parent_categories()
    export_categories()