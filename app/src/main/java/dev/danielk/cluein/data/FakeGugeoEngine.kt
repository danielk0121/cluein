package dev.danielk.cluein.data

import dev.danielk.cluein.domain.GugeoEngine
import dev.danielk.cluein.domain.GugeoRequest
import dev.danielk.cluein.domain.GugeoResponse
import dev.danielk.cluein.domain.SourceItem
import dev.danielk.cluein.domain.SourceType
import kotlinx.coroutines.delay

class FakeGugeoEngine : GugeoEngine {
    override suspend fun correct(request: GugeoRequest): GugeoResponse {
        delay(2000)
        return when {
            request.originalText.contains("사막") -> GugeoResponse(
                correctedText = "2016년 네바다 사막 Hyperloop 테스트 뉴스",
                explanation = "2005년에는 해당 기술이 상용화 단계가 아니었습니다. Hyperloop의 첫 공개 테스트는 2016년입니다.",
                probability = 95,
                sources = listOf(
                    SourceItem("2016 Hyperloop One Test — The Verge", "https://example.com/hyperloop", SourceType.NEWS),
                    SourceItem("Hyperloop One Test Video — YouTube", "https://example.com/hyperloop-video", SourceType.VIDEO),
                    SourceItem("사막 위치 추정", "", SourceType.GUESS)
                )
            )
            request.originalText.contains("영화") -> GugeoResponse(
                correctedText = "가타카 (Gattaca, 1997) — 유전자로 계급이 정해지는 세계",
                explanation = "가타카(Gattaca)는 1997년 개봉한 SF 영화로, 유전자 조작으로 태어난 사람과 자연 출생자 간의 계급 차별을 다룹니다.",
                probability = 88,
                sources = listOf(
                    SourceItem("Gattaca (1997) — IMDb", "https://example.com/gattaca", SourceType.NEWS),
                    SourceItem("가타카 공식 트레일러", "https://example.com/gattaca-trailer", SourceType.VIDEO)
                )
            )
            else -> GugeoResponse(
                correctedText = "피카츄의 꼬리 끝은 항상 노란색이었습니다.",
                explanation = "피카츄의 꼬리 끝은 처음부터 노란색입니다. 검은색 꼬리 끝은 대표적인 만델라 효과 사례입니다.",
                probability = 99,
                sources = listOf(
                    SourceItem("피카츄 공식 도감 — Bulbapedia", "https://example.com/pikachu", SourceType.NEWS)
                )
            )
        }
    }
}
