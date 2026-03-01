package dev.danielk.cluein.domain

// ─── GE-04. 입력 파싱 인터페이스 ─────────────────────────────────────────────

data class GugeoRequest(
    val originalText: String,
    val markings: List<ConfidenceMarking> = emptyList()
)

// ─── GE-05. 응답 파싱 인터페이스 ─────────────────────────────────────────────

data class GugeoResponse(
    val correctedText: String,
    val explanation: String,
    val probability: Int,
    val sources: List<SourceItem>
) {
    fun toCorrectionResult(originalText: String) = CorrectionResult(
        originalText = originalText,
        correctedText = correctedText,
        explanation = explanation,
        probability = probability,
        sources = sources
    )
}

// ─── GE-06. GugeoEngine 인터페이스 ───────────────────────────────────────────

interface GugeoEngine {
    suspend fun correct(request: GugeoRequest): GugeoResponse
}
