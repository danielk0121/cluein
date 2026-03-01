package dev.danielk.cluein

import kotlinx.coroutines.delay

/**
 * GE-06. FakeGugeoEngine — 실제 API 호출 없이 더미 데이터를 반환하는 구현체.
 * 입력 텍스트 키워드로 시나리오를 매칭하고, 없으면 기본 응답을 반환한다.
 */
class FakeGugeoEngine : GugeoEngine {

    // GE-06-1: 뉴스/영화/만델라 각 시나리오 더미 응답 데이터 3종
    private val dummyResponses = listOf(
        // 시나리오 A: 뉴스 기억 복원 (하이퍼루프)
        GugeoResponse(
            correctedText = "2016년 미국 네바다 사막에서 Hyperloop One 첫 공개 테스트가 진행됐습니다.",
            explanation = "2005년에는 하이퍼루프 기술이 존재하지 않았습니다. Elon Musk가 하이퍼루프 개념을 공개한 것은 2013년이며, Hyperloop One의 첫 실제 테스트는 2016년 5월 미국 네바다 사막에서 진행됐습니다.",
            probability = 95,
            sources = listOf(
                SourceItem("Hyperloop One First Full-Scale Test — The Verge (2016)", "https://example.com/hyperloop-2016", SourceType.NEWS),
                SourceItem("Hyperloop One Test Run — YouTube", "https://example.com/hyperloop-video", SourceType.VIDEO),
                SourceItem("사막 내 구체적 위치 추정", "", SourceType.GUESS)
            )
        ),
        // 시나리오 B: 영화/콘텐츠 기억 복원 (가타카)
        GugeoResponse(
            correctedText = "가타카 (Gattaca, 1997) — 유전자로 계급이 결정되는 디스토피아 SF 영화입니다.",
            explanation = "가타카(Gattaca)는 1997년 앤드루 니콜 감독의 SF 영화로, 유전자 조작 여부로 사회 계급이 결정되는 미래 사회를 배경으로 합니다. 등장인물들의 단정한 올백 헤어스타일이 특징이며, 2000년대가 아닌 1997년 개봉작입니다.",
            probability = 93,
            sources = listOf(
                SourceItem("Gattaca (1997) — IMDb", "https://example.com/gattaca-imdb", SourceType.NEWS),
                SourceItem("가타카 공식 트레일러 — YouTube", "https://example.com/gattaca-trailer", SourceType.VIDEO)
            )
        ),
        // 시나리오 C: 만델라 효과 (피카츄)
        GugeoResponse(
            correctedText = "피카츄의 꼬리 끝은 처음부터 항상 노란색입니다. 검은색 꼬리 끝은 존재하지 않습니다.",
            explanation = "이것은 대표적인 만델라 효과 사례입니다. 많은 사람들이 피카츄 꼬리 끝을 검은색으로 기억하지만, 포켓몬스터 초기부터 현재까지 피카츄의 꼬리 끝은 항상 노란색입니다. 귀 끝의 검은색 부분과 혼동한 것으로 추정됩니다.",
            probability = 99,
            sources = listOf(
                SourceItem("Pikachu — Bulbapedia (공식 도감)", "https://example.com/pikachu-bulbapedia", SourceType.NEWS)
            )
        )
    )

    private val defaultResponse = GugeoResponse(
        correctedText = "입력하신 기억에 대한 정확한 사실을 확인했습니다.",
        explanation = "사용자님이 입력하신 내용을 분석한 결과, 전반적으로 기억이 정확하나 일부 세부 사항은 추측입니다. 더 구체적인 정보를 입력하시면 더 정확한 보정이 가능합니다.",
        probability = 60,
        sources = listOf(
            SourceItem("구체적 출처 확인 중", "", SourceType.GUESS)
        )
    )

    // GE-06-2: 실제 API 호출 없이 키워드 매칭으로 더미 데이터 반환
    override suspend fun correct(request: GugeoRequest): GugeoResponse {
        delay(2000L) // 실제 API 호출 시뮬레이션

        val text = request.originalText.lowercase()
        return when {
            text.contains("캡슐") || text.contains("hyperloop") || text.contains("하이퍼루프") ||
            text.contains("사막") && text.contains("기차") -> dummyResponses[0]

            text.contains("역할") || text.contains("가타카") || text.contains("gattaca") ||
            text.contains("영화") && (text.contains("유전") || text.contains("올백")) -> dummyResponses[1]

            text.contains("피카츄") || text.contains("꼬리") || text.contains("만델라") ||
            text.contains("모노폴리") || text.contains("킷캣") -> dummyResponses[2]

            else -> defaultResponse
        }
    }
}
