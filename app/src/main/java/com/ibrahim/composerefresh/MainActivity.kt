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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorHost
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorRegistry
import com.ibrahim.composescrollrefresh.core.RefreshIndicatorStyle
import com.ibrahim.composescrollrefresh.pullToRefresh
import com.ibrahim.composescrollrefresh.rememberRefreshScrollState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ComposeRefreshTheme { MainScreen() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val categories = RefreshIndicatorRegistry.categories
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var selectedStyle: RefreshIndicatorStyle by remember {
        mutableStateOf(categories[0].styles[0])
    }
    var items by remember { mutableStateOf(List(20) { "Item #$it" }) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val refreshThreshold = with(density) { 80.dp.toPx() }
    val state = rememberRefreshScrollState(refreshThreshold = refreshThreshold)
    val currentCategory = categories[selectedCategoryIndex]

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
                            scope.launch { state.isRefreshing = true; delay(2000); state.endRefresh() }
                        }) { Icon(Icons.Default.Refresh, contentDescription = "Refresh") }
                    }
                )
                // Category tabs — auto-generated from registry
                ScrollableTabRow(
                    selectedTabIndex = selectedCategoryIndex,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    categories.forEachIndexed { index, category ->
                        Tab(
                            selected = selectedCategoryIndex == index,
                            onClick = {
                                selectedCategoryIndex = index
                                selectedStyle = categories[index].styles[0]
                            },
                            text = { Text(category.name) }
                        )
                    }
                }
                // Sub-style chips — auto-generated from current category
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(currentCategory.styles) { style ->
                        FilterChip(
                            selected = selectedStyle == style,
                            onClick = { selectedStyle = style },
                            label = { Text(style.key) },
                            modifier = Modifier.padding(end = 8.dp)
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
                            items = List(20) { "Refreshed #$it – ${System.currentTimeMillis() % 1000}" }
                            state.endRefresh()
                        }
                    }
                )
        ) {
            Column {
                // One composable renders any style — no when() anywhere
                RefreshIndicatorHost(state = state, style = selectedStyle)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(items) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
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