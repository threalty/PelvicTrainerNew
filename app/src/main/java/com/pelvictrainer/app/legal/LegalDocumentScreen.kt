package com.pelvictrainer.app.legal

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

enum class LegalDocument(val title: String, val assetPath: String) {
    PRIVACY("Политика конфиденциальности", "file:///android_asset/legal/privacy.html"),
    TERMS("Пользовательское соглашение", "file:///android_asset/legal/terms.html"),
    DISCLAIMER("Медицинский дисклеймер", "file:///android_asset/legal/disclaimer.html"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalDocumentScreen(
    document: LegalDocument,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(document.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.apply {
                        javaScriptEnabled = false
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = false
                        displayZoomControls = false
                        setSupportZoom(false)
                    }
                    setBackgroundColor(android.graphics.Color.WHITE)
                    loadUrl(document.assetPath)
                }
            }
        )
    }
}