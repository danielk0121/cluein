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
import dev.danielk.cluein.domain.ChatMessage
import dev.danielk.cluein.domain.ConfidenceMarking
import dev.danielk.cluein.domain.CorrectionResult
import dev.danielk.cluein.domain.DummyData
import dev.danielk.cluein.domain.GugeoRequest
import dev.danielk.cluein.domain.HistoryItem
import dev.danielk.cluein.domain.Sender
import dev.danielk.cluein.domain.SourceItem
import dev.danielk.cluein.domain.SourceType
import kotlinx.coroutines.delay

// ─── SCR-01. 홈 / 대화 화면 ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(navController: NavHostController, vm: GugeoViewModel) {
    val inputText by vm.inputText.collectAsState()
    val chatMessages by vm.chatMessages.collectAsState()
    val state by vm.state.collectAsState()
    val history by vm.history.collectAsState()
    
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    var showSettings by remember { mutableStateOf(!ApiKeyManager.hasApiKey(context)) }

    // 새로운 메시지가 추가되면 하단으로 스크롤
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            scrollState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    if (showSettings) {
        ApiKeySetupDialog(
            onDismiss = { showSettings = false },
            onSave = { apiKey ->
                ApiKeyManager.saveApiKey(context, apiKey)
                vm.updateEngine(apiKey)
                showSettings = false
            },
            onUseDummy = {
                vm.updateToDummyEngine()
                showSettings = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cluein") },
                actions = {
                    IconButton(onClick = { navController.navigate("history") }) {
                        Icon(Icons.Default.List, contentDescription = "이력")
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "설정")
                    }
                }
            )
        },
        bottomBar = {
            // 하단 입력바 (Gemini 스타일)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .imePadding()
            ) {
                if (chatMessages.isEmpty() && inputText.isBlank()) {
                    // 제안용 칩 (Suggestion Chips)
                    Text("추천 질문", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("사막에서 기차 타는 뉴스", "가타카 영화 제목", "피카츄 꼬리 색상").forEach { suggestion ->
                            SuggestionChip(
                                onClick = { vm.setInputText(suggestion) },
                                label = { Text(suggestion) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = inputText,
                            onValueChange = { vm.setInputText(it) },
                            placeholder = { Text("생각나는 단서를 입력해 보세요...") },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            maxLines = 4
                        )
                        IconButton(
                            onClick = { 
                                vm.correct(GugeoRequest(inputText))
                            },
                            enabled = inputText.isNotBlank() && state !is CorrectionState.Loading,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "전송")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (chatMessages.isEmpty()) {
            // 빈 화면: 최근 이력 미리보기
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "무엇을 찾아드릴까요?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "단편적인 기억을 단서로 사실을 복원합니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                
                if (history.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(48.dp))
                    Text("최근 보정 이력", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    history.take(3).forEach { item ->
                        HistoryCard(item) {
                            vm.setResultFromHistory(item.full)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        } else {
            // 채팅 메시지 목록
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(chatMessages) { message ->
                    ChatBubble(message, onSourceClick = { navController.navigate("sources") })
                }
                
                if (state is CorrectionState.Loading) {
                    item {
                        LoadingBubble()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeySetupDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onUseDummy: () -> Unit
) {
    var apiKey by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gemini API 설정") },
        text = {
            Column {
                Text(
                    "Cluein을 시작하기 위해 Gemini API 키가 필요합니다. 입력하신 키는 기기에만 안전하게 저장됩니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(apiKey) },
                enabled = apiKey.isNotBlank()
            ) {
                Text("저장 및 시작")
            }
        },
        dismissButton = {
            TextButton(onClick = onUseDummy) {
                Text("더미 모드 사용")
            }
        }
    )
}

@Composable
fun ChatBubble(message: ChatMessage, onSourceClick: () -> Unit) {
    val isBot = message.sender == Sender.BOT
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isBot) Alignment.Start else Alignment.End
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isBot) 4.dp else 16.dp,
                bottomEnd = if (isBot) 16.dp else 4.dp
            ),
            color = if (isBot) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
            contentColor = if (isBot) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = message.text, style = MaterialTheme.typography.bodyLarge)
                
                if (isBot && message.correctionResult != null) {
                    val result = message.correctionResult
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "신뢰도: ${result.probability}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(result.explanation, style = MaterialTheme.typography.bodySmall)
                    
                    TextButton(
                        onClick = onSourceClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("출처 보기", style = MaterialTheme.typography.labelLarge)
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingBubble() {
    val texts = listOf(
        "기억을 분석하고 있습니다...",
        "단서를 조합 중입니다...",
        "사실 관계를 확인하고 있습니다...",
        "거의 다 왔어요!"
    )
    var currentTextIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            currentTextIndex = (currentTextIndex + 1) % texts.size
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        AnimatedContent(
            targetState = texts[currentTextIndex],
            transitionSpec = {
                fadeIn() + slideInVertically { it } togetherWith fadeOut() + slideOutVertically { -it }
            },
            label = "LoadingText"
        ) { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

// ─── SCR-02. 확신도 마킹 화면 ─────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MarkingScreen(navController: NavHostController, vm: GugeoViewModel) {
    val inputText by vm.inputText.collectAsState()
    val markings by vm.markings.collectAsState()

    // 텍스트를 어절 단위로 분리
    val words = remember(inputText) {
        inputText.split(Regex("\\s+")).filter { it.isNotBlank() }
    }

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
            Text("긴가민가한 부분을 터치하여 마킹하세요.", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // 어절 선택 UI
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                FlowRow(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    words.forEach { word ->
                        val isMarked = markings.any { it.text == word }
                        FilterChip(
                            selected = isMarked,
                            onClick = {
                                if (isMarked) vm.removeMarking(word)
                                else vm.addMarking(ConfidenceMarking(0, word.length, word))
                            },
                            label = { Text(word) },
                            leadingIcon = if (isMarked) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("마킹된 항목", style = MaterialTheme.typography.titleSmall)
            
            LazyColumn(modifier = Modifier.weight(0.6f)) {
                items(markings) { marking ->
                    ListItem(
                        headlineContent = { Text("\"${marking.text}\"") },
                        supportingContent = { Text("긴가민가") },
                        trailingContent = {
                            IconButton(onClick = { vm.removeMarking(marking.text) }) {
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
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.List,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    item.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                item.inputSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "→ ${item.correctedSummary}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SourceCard(source: SourceItem) {
    val context = LocalContext.current
    val isGuess = source.type == SourceType.GUESS
    
    val icon = when (source.type) {
        SourceType.NEWS -> Icons.Default.Info
        SourceType.VIDEO -> Icons.Default.PlayArrow
        SourceType.GUESS -> Icons.Default.Warning
    }
    
    val containerColor = if (isGuess) {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = if (isGuess) BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f)) else null
    ) {
        ListItem(
            headlineContent = { 
                Text(
                    source.title,
                    fontWeight = if (isGuess) FontWeight.Normal else FontWeight.Bold
                ) 
            },
            supportingContent = { 
                if (source.url.isNotBlank()) {
                    Text(source.url, maxLines = 1, color = MaterialTheme.colorScheme.primary)
                } else if (isGuess) {
                    Text("확인된 출처가 없는 인공지능 추정 정보입니다.", style = MaterialTheme.typography.bodySmall)
                }
            },
            leadingContent = { 
                Icon(
                    icon, 
                    contentDescription = null,
                    tint = if (isGuess) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                ) 
            },
            trailingContent = {
                if (source.url.isNotBlank()) {
                    IconButton(onClick = { 
                        try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(source.url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // URL 열기 실패 처리
                        }
                    }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "열기")
                    }
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}
