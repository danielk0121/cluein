package dev.danielk.cluein

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

// ─── SCR-01. 홈 / 입력 화면 ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var inputText by remember { mutableStateOf("") }
    val recentHistory = DummyData.history.take(3)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cluein") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Settings, contentDescription = "설정")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                placeholder = { Text("그거 있잖아~\n생각나는 대로 입력해 보세요.") },
                maxLines = 8
            )

            OutlinedButton(
                onClick = { navController.navigate(Routes.MARKING) },
                modifier = Modifier.fillMaxWidth(),
                enabled = inputText.isNotBlank()
            ) {
                Text("불확실한 부분 마킹하기")
            }

            Button(
                onClick = { navController.navigate(Routes.LOADING) },
                modifier = Modifier.fillMaxWidth(),
                enabled = inputText.isNotBlank()
            ) {
                Text("기억 보정 시작")
            }

            Divider()
            Text("최근 이력", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            recentHistory.forEach { item ->
                HistoryCard(item) {
                    navController.navigate(Routes.historyDetail(item.id))
                }
            }

            OutlinedButton(
                onClick = { navController.navigate(Routes.HISTORY) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("이력 전체 보기")
            }
        }
    }
}

// ─── SCR-02. 확신도 마킹 화면 ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkingScreen(navController: NavHostController) {
    val sampleText = "사막에서 캡슐 기차 타는 뉴스, 2005년이었나?"
    val markings = listOf(ConfidenceMarking(20, 25, "2005년"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("확신도 마킹") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                },
                actions = {
                    TextButton(onClick = { navController.navigate(Routes.LOADING) }) {
                        Text("다음")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("긴가민가한 부분을\n꾹 눌러 선택하세요.", style = MaterialTheme.typography.bodyLarge)

            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = sampleText,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Text("마킹된 항목:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            markings.forEach { marking ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("\"${marking.text}\" — 긴가민가")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.navigate(Routes.LOADING) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("마킹 완료 → 보정 시작")
            }
        }
    }
}

// ─── SCR-03. 추론 중 화면 ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScreen(navController: NavHostController) {
    val messages = listOf("기억을 분석하고 있습니다...", "단서를 조합 중입니다.", "거의 다 왔어요!")
    var messageIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        repeat(messages.size - 1) { i ->
            delay(1000L)
            messageIndex = i + 1
        }
        delay(1000L)
        navController.navigate(Routes.RESULT) {
            popUpTo(Routes.LOADING) { inclusive = true }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cluein") }) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
                Text(messages[messageIndex], style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// ─── SCR-04. 보정 결과 화면 ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavHostController) {
    val result = DummyData.results[0]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("보정 결과") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CorrectionCard(label = "입력하신 기억", text = result.originalText)

            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(24.dp)
            )

            CorrectionCard(
                label = "보정된 사실",
                text = "${result.correctedText}\n\n일 확률 ${result.probability}%"
            )

            Text("보정 이유:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(result.explanation, style = MaterialTheme.typography.bodyMedium)

            Button(
                onClick = { navController.navigate(Routes.SOURCES) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("출처 / 근거 보기")
            }
        }
    }
}

// ─── SCR-05. 출처 / 근거 화면 ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcesScreen(navController: NavHostController) {
    val sources = DummyData.results[0].sources

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("출처 / 근거") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(sources) { source ->
                SourceCard(source)
            }
        }
    }
}

// ─── SCR-06. 이력 목록 화면 ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(navController: NavHostController) {
    val grouped = DummyData.history.groupBy { it.date }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("보정 이력") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            grouped.forEach { (date, items) ->
                stickyHeader {
                    Text(
                        text = date,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(items) { item ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        HistoryCard(item) {
                            navController.navigate(Routes.historyDetail(item.id))
                        }
                    }
                }
            }
        }
    }
}

// ─── SCR-07. 이력 상세 화면 ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(navController: NavHostController, historyId: String) {
    val item = DummyData.history.find { it.id == historyId } ?: DummyData.history.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("이력 상세") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(item.date, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.outline)

            CorrectionCard(label = "입력 원문", text = item.full.originalText)
            CorrectionCard(label = "보정 결과", text = "${item.full.correctedText}\n(확률 ${item.full.probability}%)")

            Button(
                onClick = { navController.navigate(Routes.SOURCES) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("출처 / 근거 보기")
            }

            OutlinedButton(
                onClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("다시 보정하기")
            }
        }
    }
}

// ─── 공통 Composable ───────────────────────────────────────────────────────────

@Composable
fun CorrectionCard(label: String, text: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(item.inputSummary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text("→ ${item.correctedSummary}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Text(item.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun SourceCard(source: SourceItem) {
    val sectionLabel = when (source.type) {
        SourceType.NEWS -> "확인된 사실"
        SourceType.VIDEO -> "관련 영상"
        SourceType.GUESS -> "추측 항목"
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (source.type == SourceType.GUESS) Icons.Default.Info else Icons.Default.Settings,
                contentDescription = null,
                tint = if (source.type == SourceType.GUESS) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(sectionLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text(source.title, style = MaterialTheme.typography.bodyMedium)
                if (source.type == SourceType.GUESS) {
                    Text("(출처 없음, 추정)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                } else {
                    Text("링크 열기 →", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
