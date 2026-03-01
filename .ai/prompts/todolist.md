# todo list (task list, 작업 목록)

## 페이즈 01. 기획/설계

- [x] 요구 사항 명세서 작성 (REQUIREMENTS.md)

## 페이즈 02. 화면 설계

- [x] 화면 설계 (와이어프레임 또는 화면 목록 정의) → .ai/assets/wireframes.md
  - [x] SCR-01. 홈 / 입력 화면: 자유 입력 텍스트필드, 입력 힌트(placeholder), 보정 시작 버튼
  - [x] SCR-02. 확신도 마킹 화면: 입력 텍스트에서 불확실 구간 선택/태그 처리 UI
  - [x] SCR-03. 추론 중 화면: 로딩 인디케이터, "기억을 분석하고 있습니다..." 안내 문구
  - [x] SCR-04. 보정 결과 화면: 원문 vs 보정 결과 대조 카드, 보정 이유 설명, 확률 표시
  - [x] SCR-05. 출처/근거 화면: 뉴스 링크·영상 목록, 추측 항목은 출처 없이 확률만 표시
  - [x] SCR-06. 이력 목록 화면: 과거 보정 이력 리스트 (날짜, 입력 원문 요약, 보정 결과 요약)
  - [x] SCR-07. 이력 상세 화면: 선택한 이력의 전체 입력 원문, 보정 결과, 출처 재표시

## 페이즈 02-B. 화면 구현 (Jetpack Compose)

- [x] SCR-01~07 Compose 화면 구현
  - [x] 의존성 추가 (navigation-compose 2.7.7)
  - [x] Models.kt — 데이터 클래스 + 더미 데이터
  - [x] NavGraph.kt — Routes + ClueinNavGraph
  - [x] Screens.kt — SCR-01~07 전체 Composable
  - [x] MainActivity.kt — NavController + ClueinNavGraph 연결

## 페이즈 03. 샘플 데이터 생성

- [x] 시나리오별 샘플 입력 데이터 작성
  - [x] SD-01. 시나리오 A (뉴스 기억 복원) 샘플 입력 5건
  - [x] SD-02. 시나리오 B (영화/콘텐츠 기억 복원) 샘플 입력 5건
  - [x] SD-03. 시나리오 C (만델라 효과/잘못된 상식 보정) 샘플 입력 5건
- [x] 시나리오별 샘플 보정 결과 데이터 작성
  - [x] SD-04. 각 샘플 입력에 대한 보정 결과 (확정 사실 + 추측 + 출처) 작성 (총 15건)
  - [x] SD-05. 이력 목록 화면(SCR-06) 기본 표시용 샘플 이력 데이터 15건 포함하여 `.ai/assets/sample_data.json` 으로 저장

## 페이즈 04. Gugeo Engine API 설계

- [x] Gugeo Engine API 인터페이스 설계 (Google Gemini API 기반)
  - [x] GE-01. Gemini API 키 발급 및 연동 확인
    - [x] GE-01-1. Google AI Studio 또는 Google Cloud Console에서 API 키 발급 (사용자 직접 발급 필요)
    - [x] GE-01-2. 앱 최초 실행 시 사용자가 직접 API 키를 입력하는 UI 구현 (SCR-08)
    - [x] GE-01-3. 입력된 키는 Android Keystore를 통해 암호화하여 런타임에만 사용 (ApiKeyManager.kt)
    - [x] GE-01-4. 소스코드, DB, 파일(local.properties 등) 어디에도 키가 포함되지 않았는지 확인
    - [x] GE-01-5. 입력된 키로 Gemini API 호출 성공 여부 확인 (더미 모드로 대체)
  - [x] GE-02. Gemini API 단순 텍스트 요청/응답 테스트 (더미 모드로 대체)
  - [x] GE-03. 기억 보정용 시스템 프롬프트 설계 → `.ai/assets/system_prompt.md`
  - [x] GE-04. 입력 파싱 인터페이스 정의 (GugeoRequest) → `GugeoEngine.kt`
  - [x] GE-05. 응답 파싱 인터페이스 정의 (GugeoResponse, GugeoEngine) → `GugeoEngine.kt`
  - [x] GE-06. 더미 API 응답 구현 (FakeGugeoEngine)
    - [x] GE-06-1. GE-05 응답 구조체를 만족하는 더미 응답 데이터 3종 작성 (뉴스/영화/만델라 각 시나리오)
    - [x] GE-06-2. FakeGugeoEngine 클래스 구현 — 키워드 매칭으로 더미 데이터 반환
    - [x] GE-06-3. FakeGugeoEngine → GugeoViewModel → SCR-04 보정 결과 화면 연동
    - [x] GE-06-4. FakeGugeoEngine → GugeoViewModel → SCR-05 출처/근거 화면 연동
  - [x] GE-07. Android 프로젝트에 Gemini SDK(google-generativeai) 의존성 추가
  - [x] GE-08. GugeoEngine 클래스 구현 및 Gemini API 호출 연동 (FakeGugeoEngine 교체)
  - [x] GE-09. 실제 시나리오(뉴스/영화/만델라)로 엔드투엔드 연동 테스트

## 페이즈 05. 핵심 기능 구현

- [x] FR-01. 자유 입력 인터페이스 구현 (메인 입력 화면)
- [x] FR-02. 확신도 표시 UI 구현 (흐릿함 마킹)
- [x] FR-03. Gugeo Engine 연동 (Google Gemini API 호출 및 추론 로직)
- [x] FR-04. 보정 결과 화면 구현
- [x] FR-05. 출처/근거 표시 UI 구현
- [ ] FR-06. 보정 이력 저장 및 조회 구현

## 페이즈 06. 인프라/기타

- [ ] 프로젝트 아키텍처 설계 (폴더 구조, 레이어 분리)
- [ ] 의존성 최종 확인 및 빌드 검증
