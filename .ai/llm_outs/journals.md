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

## 2026-03-01T10:32:17+09:00

**작업 내용**: 페이즈 02-B SCR-01~07 Jetpack Compose 화면 구현

- `gradle/libs.versions.toml` — `navigationCompose = "2.7.7"` 및 `androidx-navigation-compose` 라이브러리 항목 추가
- `app/build.gradle.kts` — `implementation(libs.androidx.navigation.compose)` 의존성 추가
- `Models.kt` 신규 생성
  - `ConfidenceMarking`, `SourceItem`, `SourceType`, `CorrectionResult`, `HistoryItem` 데이터 클래스
  - `DummyData` object — 더미 보정 결과 3건, 이력 3건
- `NavGraph.kt` 신규 생성
  - `Routes` object — HOME, MARKING, LOADING, RESULT, SOURCES, HISTORY, HISTORY_DETAIL 상수
  - `ClueinNavGraph` — NavHost로 7개 라우트 연결
- `Screens.kt` 신규 생성
  - SCR-01 `HomeScreen`: 입력 필드, 마킹 버튼, 보정 시작 버튼, 최근 이력 3건
  - SCR-02 `MarkingScreen`: 텍스트 + 마킹 목록 (더미)
  - SCR-03 `LoadingScreen`: CircularProgressIndicator + LaunchedEffect 3초 후 Result 이동
  - SCR-04 `ResultScreen`: 원문/보정 결과 카드 + 이유 + 출처 버튼
  - SCR-05 `SourcesScreen`: LazyColumn 출처 카드 목록
  - SCR-06 `HistoryScreen`: 날짜별 stickyHeader + HistoryCard 목록
  - SCR-07 `HistoryDetailScreen`: 원문/결과 카드 + 출처/재보정 버튼
  - 공통 Composable: `CorrectionCard`, `HistoryCard`, `SourceCard`
- `MainActivity.kt` 수정 — Greeting 제거, NavController + ClueinNavGraph 연결
- `./gradlew assembleDebug` 빌드 성공 확인 (경고: deprecated Divider/ArrowBack, 에러 없음)
- `todolist.md` 페이즈 02-B 완료([x]) 처리

## 2026-03-01T10:45:00+09:00

**작업 내용**: 페이즈 03 샘플 데이터 생성 완료 (SD-01~05)

- `.ai/assets/sample_data.json` 생성
  - 시나리오 A (뉴스 기억 복원) 샘플 입력 5건 (A-01~05)
  - 시나리오 B (영화/콘텐츠 기억 복원) 샘플 입력 5건 (B-01~05)
  - 시나리오 C (만델라 효과/잘못된 상식 보정) 샘플 입력 5건 (C-01~05)
  - 각 시나리오별 보정 결과 15건 (확정 사실 + 추측 + 출처)
  - 이력 목록 화면(SCR-06) 표시용 샘플 이력 15건
- `todolist.md` SD-01~05 완료([x]) 처리

