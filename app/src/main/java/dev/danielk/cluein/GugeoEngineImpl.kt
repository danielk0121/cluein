package dev.danielk.cluein

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import org.json.JSONObject

/**
 * GE-08. GugeoEngine 실제 구현체 — Google Gemini API 호출.
 * API 키는 런타임에만 ApiKeyManager를 통해 제공받으며, 소스코드에 포함하지 않는다.
 */
class GugeoEngineImpl(private val apiKey: String) : GugeoEngine {

    private val systemPrompt = """
당신은 Gugeo Engine입니다. 사용자의 불완전하고 모호한 기억을 분석하여 실제 사실과 대조하고 보정하는 추론 전문가입니다.

반드시 아래 JSON 형식으로만 응답하십시오. 다른 텍스트를 포함하지 마십시오.

{
  "correctedText": "보정된 사실 요약 (1~3문장)",
  "explanation": "보정 이유 설명 (친절하고 자세하게)",
  "probability": 95,
  "sources": [
    {
      "title": "출처 제목",
      "url": "https://...",
      "type": "NEWS"
    }
  ]
}

type 값: NEWS(뉴스/공식문서), VIDEO(영상), GUESS(추측, url은 빈 문자열)
추측 항목은 반드시 type을 GUESS로 표시하고 url을 비운다.
probability는 0~100 정수.
응답 언어는 항상 한국어 (url, type 제외).
""".trimIndent()

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey,
            systemInstruction = content { text(systemPrompt) }
        )
    }

    override suspend fun correct(request: GugeoRequest): GugeoResponse {
        val markingDesc = if (request.markings.isEmpty()) {
            ""
        } else {
            "\n불확실한 부분: ${request.markings.joinToString(", ") { "\"${it.text}\"" }}"
        }
        val prompt = "원문: ${request.originalText}$markingDesc"

        val response = model.generateContent(prompt)
        val text = response.text ?: throw Exception("응답이 비어 있습니다.")

        return parseResponse(text)
    }

    private fun parseResponse(text: String): GugeoResponse {
        val jsonStr = text
            .trimIndent()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val json = JSONObject(jsonStr)
        val sourcesArray = json.getJSONArray("sources")
        val sources = (0 until sourcesArray.length()).map { i ->
            val s = sourcesArray.getJSONObject(i)
            SourceItem(
                title = s.getString("title"),
                url = s.optString("url", ""),
                type = when (s.getString("type")) {
                    "VIDEO" -> SourceType.VIDEO
                    "GUESS" -> SourceType.GUESS
                    else -> SourceType.NEWS
                }
            )
        }

        return GugeoResponse(
            correctedText = json.getString("correctedText"),
            explanation = json.getString("explanation"),
            probability = json.getInt("probability"),
            sources = sources
        )
    }
}
