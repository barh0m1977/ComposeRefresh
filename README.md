# Compose Scroll Refresh

A lightweight, secure, and customizable Pull-to-Refresh library for Jetpack Compose.
[![](https://jitpack.io/v/barh0m1977/ComposeRefresh.svg)](https://jitpack.io/#barh0m1977/ComposeRefresh)
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
    implementation("com.github.barh0m1977:ComposeRefresh:1.0.0")
}
```

## ✨ Features

- **Smooth Animations**: Powered by Compose `Animatable`.
- **Customizable**: Control thresholds and refresh indicators easily.
- **Secure**: Pre-configured with R8/ProGuard obfuscation rules.
- **Lightweight**: Zero heavy dependencies beyond standard Compose libraries.

## 🛠 Usage

```kotlin
val state = rememberRefreshScrollState(refreshThreshold = 80.dp)

Box(modifier = Modifier.pullToRefresh(
    state = state,
    onRefresh = { 
        // Trigger your refresh logic here
        viewModel.refreshData() 
    }
)) {
    // Your scrollable content (LazyColumn, Column with verticalScroll, etc.)
}
```

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
