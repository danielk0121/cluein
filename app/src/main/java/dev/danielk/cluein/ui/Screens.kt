package dev.danielk.cluein.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.danielk.cluein.data.ApiKeyManager
import dev.danielk.cluein.domain.*
import kotlinx.coroutines.delay

// ─── SCR-08. API 키 설정 화면 ────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeySetupScreen(navController: NavHostController) {
    var apiKey by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gemini API 설정") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Cluein을 시작하기 위해\nGemini API 키가 필요합니다.",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "입력하신 키는 기기에만 안전하게 저장됩니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (apiKey.isNotBlank()) {
                        ApiKeyManager.saveApiKey(context, apiKey)
                        navController.navigate("home") {
                            popUpTo("api_key_setup") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = apiKey.isNotBlank()
            ) {
                Text("저장하고 시작하기")
            }

            TextButton(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("api_key_setup") { inclusive = true }
                    }
                }
            ) {
                Text("나중에 설정 (더미 모드)")
            }
        }
    }
}

// ─── SCR-01. 홈 / 입력 화면 ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, vm: GugeoViewModel) {
    val inputText by vm.inputText.collectAsState()
    val history by vm.history.collectAsState()
    val recentHistory = history.take(3)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cluein") },
                actions = {
                    IconButton(onClick = { navController.navigate("api_key_setup") }) {
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
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "그거 있잖아~",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = inputText,
                        onValueChange = { vm.setInputText(it) },
                        placeholder = { Text("생각나는 대로 입력해 보세요.\n예: 사막에서 캡슐 기차 타는 뉴스, 2005년이었나?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { navController.navigate("marking") },
                    modifier = Modifier.weight(1f),
                    enabled = inputText.isNotBlank()
                ) {
                    Text("불확실한 부분 마킹")
                }
                Button(
                    onClick = { 
                        vm.correct(GugeoRequest(inputText))
                        navController.navigate("loading") 
                    },
                    modifier = Modifier.weight(1f),
                    enabled = inputText.isNotBlank()
                ) {
                    Text("기억 보정 시작")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("최근 이력", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))

            if (recentHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("최근 이력이 없습니다.", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                recentHistory.forEach { item ->
                    HistoryCard(item) {
                        navController.navigate("history_detail/${item.id}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                TextButton(
                    onClick = { navController.navigate("history") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("이력 전체 보기")
                }
            }
        }
    }
}

// ─── SCR-02. 확신도 마킹 화면 ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkingScreen(navController: NavHostController, vm: GugeoViewModel) {
    val inputText by vm.inputText.collectAsState()
    val markings by vm.markings.collectAsState()

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
                    TextButton(onClick = { 
                        vm.correct(GugeoRequest(inputText, markings))
                        navController.navigate("loading") 
                    }) {
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
                .padding(16.dp)
        ) {
            Text("긴가민가한 부분을 선택하세요.", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // 더미 마킹 UI: 실제 텍스트 선택 로직은 복잡하므로 간단한 태그 예시로 대체
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(inputText)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("마킹된 항목", style = MaterialTheme.typography.titleSmall)
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(markings) { marking ->
                    ListItem(
                        headlineContent = { Text("\"${marking.text}\"") },
                        supportingContent = { Text("긴가민가") },
                        trailingContent = {
                            IconButton(onClick = { /* Remove */ }) {
                                Icon(Icons.Default.Close, contentDescription = "삭제")
                            }
                        }
                    )
                }
            }

            Button(
                onClick = { 
                    vm.correct(GugeoRequest(inputText, markings))
                    navController.navigate("loading") 
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("마킹 완료 → 보정 시작")
            }
        }
    }
}

// ─── SCR-03. 추론 중 화면 ─────────────────────────────────────────────────────

@Composable
fun LoadingScreen(navController: NavHostController, vm: GugeoViewModel) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state) {
        if (state is CorrectionState.Success) {
            navController.navigate("result") {
                popUpTo("loading") { inclusive = true }
            }
        } else if (state is CorrectionState.Error) {
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(24.dp))
        Text("기억을 분석하고 있습니다...", style = MaterialTheme.typography.titleMedium)
        Text("단서를 조합 중입니다.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
    }
}

// ─── SCR-04. 보정 결과 화면 ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavHostController, vm: GugeoViewModel) {
    val state by vm.state.collectAsState()
    val result = (state as? CorrectionState.Success)?.result ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("보정 결과") },
                navigationIcon = {
                    IconButton(onClick = { 
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "홈")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "공유")
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
            
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
            }

            CorrectionCard(
                label = "보정된 사실",
                text = result.correctedText,
                probability = result.probability,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )

            Text("보정 이유:", style = MaterialTheme.typography.titleSmall)
            Text(result.explanation, style = MaterialTheme.typography.bodyLarge)

            Button(
                onClick = { navController.navigate("sources") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("출처 / 근거 보기")
            }
        }
    }
}

// ─── SCR-05. 출처 / 근거 화면 ──────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcesScreen(navController: NavHostController, vm: GugeoViewModel) {
    val state by vm.state.collectAsState()
    val sources = (state as? CorrectionState.Success)?.result?.sources ?: emptyList()

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
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
fun HistoryScreen(navController: NavHostController, vm: GugeoViewModel) {
    val history by vm.history.collectAsState()
    val grouped = history.groupBy { it.date }

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
        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("보정 이력이 없습니다.")
            }
        } else {
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
                                navController.navigate("history_detail/${item.id}")
                            }
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
fun HistoryDetailScreen(navController: NavHostController, historyId: String, vm: GugeoViewModel) {
    val history by vm.history.collectAsState()
    val item = history.find { it.id == historyId } ?: return

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
                onClick = {
                    vm.setResultFromHistory(item.full)
                    navController.navigate("sources")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("출처 / 근거 보기")
            }

            OutlinedButton(
                onClick = {
                    vm.setInputText(item.full.originalText)
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("다시 보정하기")
            }
        }
    }
}

// ─── 공통 Composable ─────────────────────────────────────────────────────────

@Composable
fun CorrectionCard(
    label: String,
    text: String,
    probability: Int? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                if (probability != null) {
                    Text("확률 $probability%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.inputSummary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(4.dp))
            Text("→ ${item.correctedSummary}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.date, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.End))
        }
    }
}

@Composable
fun SourceCard(source: SourceItem) {
    val icon = when (source.type) {
        SourceType.NEWS -> Icons.Default.Info
        SourceType.VIDEO -> Icons.Default.PlayArrow
        SourceType.GUESS -> Icons.Default.Warning
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        ListItem(
            headlineContent = { Text(source.title) },
            supportingContent = { 
                if (source.url.isNotBlank()) Text(source.url, maxLines = 1) 
                else if (source.type == SourceType.GUESS) Text("출처 없음, 추정")
            },
            leadingContent = { Icon(icon, contentDescription = null) },
            trailingContent = {
                if (source.url.isNotBlank()) {
                    IconButton(onClick = { /* Open URL */ }) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "열기")
                    }
                }
            }
        )
    }
}
