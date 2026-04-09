# Compose Scroll Refresh

A lightweight, secure, and customizable Pull-to-Refresh library for Jetpack Compose.
## [![](https://jitpack.io/v/barh0m1977/ComposeRefresh.svg)](https://jitpack.io/#barh0m1977/ComposeRefresh)

## Video
https://github.com/user-attachments/assets/274ce14b-c8ab-4fa2-bec1-98d795faf04f

## 🚀 Installation

Add JitPack to your `settings.gradle.kts`:

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}
```

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.barh0m1977:ComposeRefresh:1.1.0")
}
```

## ✨ Features

- **Smooth Animations**: Powered by Compose `Animatable`.
- **Customizable**: Control thresholds and refresh indicators easily.
- **Easy-to-use** `pullToRefresh` modifier
- **Multiple refresh** indicator styles
- **Lightweight**: Zero heavy dependencies beyond standard Compose libraries.

## 🛠 Usage

```kotlin
. Pick a style
var selectedStyle by remember { mutableStateOf(RefreshIndicatorRegistry.wave.classicWave) }

@Composable
fun SampleRefreshScreen() {
    var items by remember { mutableStateOf(List(20) { "Item #$it" }) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val state = rememberRefreshScrollState(
        refreshThreshold = with(density) { 80.dp.toPx() }
    )
    var selectedStyle by remember { 
        mutableStateOf(RefreshIndicatorRegistry.wave.classicWave) 
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(
                state = state,
                onRefresh = {
                    scope.launch {
                        delay(2000) // simulate network call
                        items = List(20) { "Refreshed #$it" }
                        state.endRefresh()
                    }
                }
            )
    ) {
        Column {
          RefreshIndicatorHost(state = state, style = selectedStyle)

            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(items) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Box(modifier = Modifier.padding(24.dp)) {
                            Text(text = item)
                        }
                    }
                }
            }
        }
    }
}
```
``` Programmatically Trigger Refresh
IconButton(onClick = {
    scope.launch {
        state.isRefreshing = true
        delay(2000)
        state.endRefresh()
    }
}) {
    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
}
```
## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
