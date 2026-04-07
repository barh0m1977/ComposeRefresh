package com.ibrahim.composerefresh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ibrahim.composerefresh.ui.theme.ComposeRefreshTheme
import com.ibrahim.composescrollrefresh.pullToRefresh
import com.ibrahim.composescrollrefresh.rememberRefreshScrollState
import com.ibrahim.composescrollrefresh.styles.AdvancedRefreshIndicator
import com.ibrahim.composescrollrefresh.styles.BubbleRefreshIndicator
import com.ibrahim.composescrollrefresh.styles.SpringRefreshIndicator
import com.ibrahim.composescrollrefresh.styles.WaveRefreshIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeRefreshTheme {
                MainScreen()
            }
        }
    }
}

enum class RefreshStyle(val title: String) {
    Wave("Wave"),
    Bubble("Bubble"),
    Spring("Spring"),
    Advanced("Advanced")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedStyle by remember { mutableStateOf(RefreshStyle.Wave) }
    var items by remember { mutableStateOf(List(20) { "Item #$it" }) }
    val scope = rememberCoroutineScope()
    
    val density = LocalDensity.current
    val refreshThreshold = with(density) { 80.dp.toPx() }
    val state = rememberRefreshScrollState(refreshThreshold = refreshThreshold)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("ComposeRefresh") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    actions = {
                        IconButton(onClick = { 
                            scope.launch { 
                                state.isRefreshing = true 
                                delay(2000)
                                state.endRefresh()
                            }
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                )
                ScrollableTabRow(
                    selectedTabIndex = selectedStyle.ordinal,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    RefreshStyle.entries.forEach { style ->
                        Tab(
                            selected = selectedStyle == style,
                            onClick = { selectedStyle = style },
                            text = { Text(style.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pullToRefresh(
                    state = state,
                    onRefresh = {
                        scope.launch {
                            delay(2000)
                            items = List(20) { "Refreshed Item #$it ${System.currentTimeMillis() % 1000}" }
                            state.endRefresh()
                        }
                    }
                )
        ) {
            Column {
                // The indicator itself
                RefreshIndicator(state = state, style = selectedStyle)
                
                // The scrollable content
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(items) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(text = item, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RefreshIndicator(state: com.ibrahim.composescrollrefresh.RefreshScrollState, style: RefreshStyle) {
    when (style) {
        RefreshStyle.Wave -> WaveRefreshIndicator(state = state)
        RefreshStyle.Bubble -> BubbleRefreshIndicator(state = state)
        RefreshStyle.Spring -> SpringRefreshIndicator(state = state)
        RefreshStyle.Advanced -> AdvancedRefreshIndicator(state = state)
    }
}
