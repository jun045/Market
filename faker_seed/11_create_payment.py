# 📄 faker_seed/generate_payments.py
import csv
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent
DATA_DIR = BASE_DIR / "data_seed"
DATA_DIR.mkdir(parents=True, exist_ok=True)

PURCHASE_ORDERS_UPDATED_CSV = DATA_DIR / "purchase_orders_updated.csv"
PAYMENTS_CSV = DATA_DIR / "payments.csv"


def generate_payments():
    if not PURCHASE_ORDERS_UPDATED_CSV.exists():
        raise FileNotFoundError(f"purchase_orders_updated.csv를 찾을 수 없습니다: {PURCHASE_ORDERS_UPDATED_CSV}")

    payments = []

    with open(PURCHASE_ORDERS_UPDATED_CSV, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)

        for row in reader:
            payment_id = (row.get("payment_id") or "").strip()
            if payment_id == "":
                # payment_id 없는 주문은 결제 레코드 생성 X
                continue

            member_id = int(row["member_id"])
            purchase_order_id = int(row["id"])

            imp_uid = (row.get("paid_imp_uid") or "").strip()
            merchant_uid = (row.get("merchant_uid") or "").strip()

            # paid_at은 주문 paid_at이 정석, 없으면 created_at fallback
            paid_at = (row.get("paid_at") or "").strip()
            if paid_at == "":
                paid_at = (row.get("created_at") or "").strip()

            payments.append({
                "id": int(payment_id),
                "member_id": member_id,
                "purchase_order_id": purchase_order_id,
                "imp_uid": imp_uid,
                "merchant_uid": merchant_uid,
                "pay_method": "card",
                "pay_status": "PAID",
                "amount": "100",  # BigDecimal 컬럼이어도 CSV는 문자열/정수로 넣으면 됨
                "pg_provider": "html5_inicis",
                "paid_at": paid_at,
            })

    print(f"✅ payments 생성 완료: {len(payments):,}개")
    return payments


def export_payments_to_csv(payments):
    headers = [
        "id",
        "member_id",
        "purchase_order_id",
        "imp_uid",
        "merchant_uid",
        "pay_method",
        "pay_status",
        "amount",
        "pg_provider",
        "paid_at",
    ]

    with open(PAYMENTS_CSV, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=headers)
        writer.writeheader()
        writer.writerows(payments)

    print(f"💾 payments.csv 저장 완료 → {PAYMENTS_CSV}")


if __name__ == "__main__":
    payments = generate_payments()
    export_payments_to_csv(payments)
