# 작업 일지 (journal)

## 2026-03-01 09:17 +09:00

**작업 내용**: 프로젝트 요구 사항 명세서 및 할일 목록 초안 작성

- `REQUIREMENTS.md` 작성
  - 프로젝트 개요 및 포지셔닝 정의 ("검색과 상담 사이")
  - 핵심 사용 시나리오 3가지 정의 (뉴스 복원, 영화 기억 복원, 만델라 효과 보정)
  - 기능 요구 사항 FR-01 ~ FR-06 정의
  - 비기능 요구 사항 (Android minSdk 26 / targetSdk 34, Kotlin/Compose)
  - 용어 정의 (Gugeo Engine, 설단 현상 등)
- `todolist.md` 작성
  - 기획/설계, 핵심 기능 구현, 인프라/기타 카테고리로 구분
  - 요구 사항 명세서 작성 항목 완료([x]) 처리

## 2026-03-01T09:50:38+09:00

**작업 내용**: README.md 작성

- REQUIREMENTS.md 기반으로 프로젝트 루트 `README.md` 작성
  - 프로젝트 개요 및 포지셔닝
  - 핵심 사용 시나리오 3종 (뉴스/영화/만델라)
  - 주요 기능 요약 (자유 입력, 확신도 마킹, Gugeo Engine, 출처 제공, 보정 이력)
  - 기술 스택 (Android minSdk 26 / targetSdk 34, Kotlin, Jetpack Compose, Gemini API)
  - 보안 정책 요약
  - 용어 정의

## 2026-03-01T10:05:00+09:00

**작업 내용**: CLAUDE.md 및 .claude/memory/MEMORY.md 설정

- `~/.claude/projects/.../memory/MEMORY.md` → `.claude/memory/MEMORY.md` 로 이동
- 프로젝트 루트에 `CLAUDE.md` 생성
  - 세션 시작 시 `.claude/memory/MEMORY.md`, `.ai/prompts/` 파일 자동 참고 지시

## 2026-03-01T11:30:00+09:00

**작업 내용**: 페이즈 02 화면 설계 완료 (SCR-01~07)

- `.ai/assets/wireframes.md` 생성
  - SCR-01. 홈/입력 화면: 자유 입력 텍스트필드, 최근 이력 미리보기
  - SCR-02. 확신도 마킹 화면: 불확실 구간 선택/태그 UI
  - SCR-03. 추론 중 화면: 로딩 인디케이터 + 단계별 안내 문구
  - SCR-04. 보정 결과 화면: 원문 vs 보정 대조 카드 + 이유 설명
  - SCR-05. 출처/근거 화면: 뉴스 링크·영상 카드, 추측 항목 확률만 표시
  - SCR-06. 이력 목록 화면: 날짜별 섹션, 아이템 카드
  - SCR-07. 이력 상세 화면: 전체 원문·보정 결과, 재보정 버튼
- `todolist.md` 페이즈 02 완료([x]) 처리

