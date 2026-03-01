package dev.danielk.cluein

data class ConfidenceMarking(val start: Int, val end: Int, val text: String)

enum class SourceType { NEWS, VIDEO, GUESS }

data class SourceItem(val title: String, val url: String, val type: SourceType)

data class CorrectionResult(
    val originalText: String,
    val correctedText: String,
    val explanation: String,
    val probability: Int,
    val sources: List<SourceItem>
)

data class HistoryItem(
    val id: String,
    val date: String,
    val inputSummary: String,
    val correctedSummary: String,
    val full: CorrectionResult
)

object DummyData {
    val results = listOf(
        CorrectionResult(
            originalText = "사막에서 캡슐 기차 타는 뉴스, 2005년이었나?",
            correctedText = "2016년 네바다 사막 Hyperloop 테스트 뉴스",
            explanation = "2005년에는 해당 기술이 상용화 단계가 아니었습니다. Hyperloop의 첫 공개 테스트는 2016년입니다.",
            probability = 95,
            sources = listOf(
                SourceItem("2016 Hyperloop One Test — The Verge", "https://example.com/hyperloop", SourceType.NEWS),
                SourceItem("Hyperloop One Test Video — YouTube", "https://example.com/hyperloop-video", SourceType.VIDEO),
                SourceItem("사막 위치 추정", "", SourceType.GUESS)
            )
        ),
        CorrectionResult(
            originalText = "역할이 정해진 영화, 2000년대 초 SF 영화였는데...",
            correctedText = "가타카 (Gattaca, 1997) — 유전자로 계급이 정해지는 세계",
            explanation = "가타카(Gattaca)는 1997년 개봉한 SF 영화로, 유전자 조작으로 태어난 사람과 자연 출생자 간의 계급 차별을 다룹니다.",
            probability = 88,
            sources = listOf(
                SourceItem("Gattaca (1997) — IMDb", "https://example.com/gattaca", SourceType.NEWS),
                SourceItem("가타카 공식 트레일러", "https://example.com/gattaca-trailer", SourceType.VIDEO)
            )
        ),
        CorrectionResult(
            originalText = "피카츄 꼬리 끝이 검은색이었던 것 같은데?",
            correctedText = "피카츄의 꼬리 끝은 항상 노란색이었습니다.",
            explanation = "피카츄의 꼬리 끝은 처음부터 노란색입니다. 검은색 꼬리 끝은 대표적인 만델라 효과 사례입니다.",
            probability = 99,
            sources = listOf(
                SourceItem("피카츄 공식 도감 — Bulbapedia", "https://example.com/pikachu", SourceType.NEWS)
            )
        )
    )

    val history = listOf(
        HistoryItem("h1", "2026-02-28", "사막에서 캡슐 기차 뉴스…", "2016년 Hyperloop 테스트", results[0]),
        HistoryItem("h2", "2026-02-25", "역할이 정해진 영화 2000…", "가타카 (Gattaca, 1997)", results[1]),
        HistoryItem("h3", "2026-02-20", "피카츄 꼬리 끝이 검은색…", "피카츄 꼬리는 노란색", results[2])
    )
}
