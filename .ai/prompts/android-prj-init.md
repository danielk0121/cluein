## 안드로이드 프로젝트 생성 시 기본적으로 진행 하는 AI 프롬프트 작업
이 문서는 프로젝트 최초 생성시에만 참고된다.

1. 앱이름, 패키지 이름 설정 (설정된 값이 없을 경우 사용자에게 반드시 확인 받아야 함!)
2. 언어는 코틀린 으로 설정, 자바 17 버전 기준으로 설정
3. `gradle/libs.versions.toml` — minSdk 26 / targetSdk 34 기준으로 의존성 버전 수정
4. `.ai/prompts/REQUIREMENTS.md` — 요구 사항 명세서 초안 작성
5. `.ai/prompts/todolist.md` — 할일 목록 초안 작성
6. `REQUIREMENTS.md` — "비기능 요구 사항" → "프로젝트 기술 요구 사항" 명칭 변경
7. `todolist.md` — 화면 설계를 구체적으로 세부 항목 추가
8. `todolist.md` — API 키 연동 작업이 있으면 자세한 세부 항목 추가
9. `REQUIREMENTS.md` — API 키 연동 작업에 대해, 보안 항목 추가 (키를 소스/DB/파일에 저장 금지)
10. `todolist.md` — 더미 API 응답 세부 항목 추가
11. `todolist.md` — 각 화면에 대해 샘플 데이터 생성 항목 추가
12. `todolist.md` — 샘플 데이터 중 목록에 해당하는 샘플 데이터는 기본값 15건으로 변경
