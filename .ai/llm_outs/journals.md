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

## 2026-03-01T11:00:00+09:00

**작업 내용**: GE-01-2~4 API 키 입력 UI + Keystore 암호화 구현

- `ApiKeyManager.kt` 신규 생성
  - Android Keystore AES/GCM 암호화로 Gemini API 키 저장/로드
  - `saveApiKey`, `loadApiKey`, `hasApiKey`, `clearApiKey` API 제공
  - 소스코드·파일·DB에 평문 키 미저장 구조
- `Screens.kt` — `ApiKeySetupScreen(SCR-08)` 추가
  - 최초 실행 시 API 키 입력 UI (PasswordVisualTransformation)
  - 더미 모드로 시작 버튼 (FakeGugeoEngine 대응)
- `NavGraph.kt` — `API_KEY_SETUP` 라우트 추가, 키 유무에 따라 시작 화면 분기
- 소스코드 내 평문 API 키 미포함 검증 완료 (grep 확인)
- `todolist.md` GE-01-2~4 완료([x]) 처리

## 2026-03-01T11:15:00+09:00

**작업 내용**: GE-03~05 시스템 프롬프트 + 입출력 파싱 인터페이스 설계

- `.ai/assets/system_prompt.md` 생성 (GE-03)
  - 역할 정의, JSON 응답 형식, type 값 규칙(NEWS/VIDEO/GUESS), 응답 규칙 6가지
- `GugeoEngine.kt` 신규 생성 (GE-04, GE-05)
  - `GugeoRequest`: 사용자 원문 + 확신도 마킹 목록 → 요청 구조체
  - `GugeoResponse`: 보정 결과 + 확률 + 출처 → 응답 구조체, `toCorrectionResult()` 변환 제공
  - `GugeoEngine` interface: `suspend fun correct(request)` 정의
- `todolist.md` GE-03~05 완료([x]) 처리

## 2026-03-01T11:30:00+09:00

**작업 내용**: GE-06 FakeGugeoEngine 구현 및 SCR-04/05 UI 연동

- `FakeGugeoEngine.kt` 신규 생성 (GE-06-1~2)
  - 뉴스(하이퍼루프)/영화(가타카)/만델라(피카츄) 3종 더미 응답 데이터
  - 키워드 매칭으로 시나리오 분기, 2초 delay로 실제 API 시뮬레이션
- `GugeoViewModel.kt` 신규 생성
  - `CorrectionState` sealed class (Idle/Loading/Success/Error)
  - inputText, markings StateFlow 상태 관리
  - `correct()`, `setInputText()`, `setResultFromHistory()` API
- `Screens.kt` GugeoViewModel 연동 (GE-06-3~4)
  - HomeScreen: 입력 텍스트 → vm.correct() → LOADING 이동
  - MarkingScreen: vm.inputText/markings 상태 표시
  - LoadingScreen: CorrectionState 변화 감지 → RESULT 자동 이동
  - ResultScreen: CorrectionState.Success 결과 표시
  - SourcesScreen: Success 결과의 sources 표시
  - HistoryDetailScreen: setResultFromHistory() → SOURCES 연동
- `NavGraph.kt`: GugeoViewModel 전달 구조로 업데이트
- assembleDebug 빌드 성공
- `todolist.md` GE-06 완료([x]) 처리

## 2026-03-01T11:45:00+09:00

**작업 내용**: GE-07~09 GugeoEngineImpl 연동 및 GugeoViewModelFactory 구현

- `GugeoViewModel.kt` 수정
  - `GugeoViewModel`이 `GugeoEngine`을 생성자로 주입받도록 구조 변경
  - `GugeoViewModelFactory` 추가: `ApiKeyManager`를 통해 API 키 유무를 확인하고 `GugeoEngineImpl` 또는 `FakeGugeoEngine`을 동적으로 주입
- `NavGraph.kt` 수정
  - `ClueinNavGraph`에서 `GugeoViewModelFactory`를 사용하여 ViewModel 생성
- `GE-07` Gemini SDK 의존성(`google-generativeai`) 확인 (이미 추가됨)
- `GE-08` `GugeoEngineImpl` 실제 구현체 연동 완료
- `GE-09` 빌드 테스트 성공 (`./gradlew assembleDebug`)
- `todolist.md` 페이즈 04 전체 및 페이즈 05 (FR-01~05) 완료([x]) 처리

