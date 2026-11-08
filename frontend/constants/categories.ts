export interface ChildCategory {
  id: number;
  name: string;
}

export interface ParentCategory {
  id: number;
  no: string;
  name: string;
  children: ChildCategory[];
}

export const PARENT_CATEGORIES: ParentCategory[] = [
  {
    id: 1,
    no: "01",
    name: "패션의류",
    children: [
      { id: 1, name: "남성의류" },
      { id: 2, name: "여성의류" },
      { id: 3, name: "아동복" },
    ],
  },
  {
    id: 2,
    no: "02",
    name: "패션잡화",
    children: [
      { id: 4, name: "신발" },
      { id: 5, name: "가방" },
      { id: 6, name: "액세서리" },
    ],
  },
  {
    id: 3,
    no: "03",
    name: "디지털/가전",
    children: [
      { id: 7, name: "스마트폰" },
      { id: 8, name: "노트북" },
      { id: 9, name: "가전제품" },
      { id: 10, name: "웨어러블" },
    ],
  },
  {
    id: 4,
    no: "04",
    name: "뷰티",
    children: [
      { id: 11, name: "스킨케어" },
      { id: 12, name: "메이크업" },
      { id: 13, name: "향수" },
    ],
  },
  {
    id: 5,
    no: "05",
    name: "식품",
    children: [
      { id: 14, name: "신선식품" },
      { id: 15, name: "간편식" },
      { id: 16, name: "음료" },
    ],
  },
  {
    id: 6,
    no: "06",
    name: "생활/주방",
    children: [
      { id: 17, name: "청소용품" },
      { id: 18, name: "주방용품" },
      { id: 19, name: "욕실용품" },
    ],
  },
  {
    id: 7,
    no: "07",
    name: "가구/인테리어",
    children: [
      { id: 20, name: "거실가구" },
      { id: 21, name: "조명" },
      { id: 22, name: "수납정리" },
    ],
  },
  {
    id: 8,
    no: "08",
    name: "스포츠/레저",
    children: [
      { id: 23, name: "운동화" },
      { id: 24, name: "캠핑용품" },
      { id: 25, name: "자전거" },
      { id: 26, name: "헬스기구" },
    ],
  },
  {
    id: 9,
    no: "09",
    name: "도서/취미",
    children: [
      { id: 27, name: "소설" },
      { id: 28, name: "음악/악기" },
      { id: 29, name: "문구용품" },
    ],
  },
  {
    id: 10,
    no: "10",
    name: "반려동물",
    children: [
      { id: 30, name: "사료" },
      { id: 31, name: "장난감" },
      { id: 32, name: "미용용품" },
    ],
  },
];

export function findChildCategoryName(childId: number): string | null {
  for (const parent of PARENT_CATEGORIES) {
    const child = parent.children.find((c) => c.id === childId);
    if (child) return child.name;
  }
  return null;
}
