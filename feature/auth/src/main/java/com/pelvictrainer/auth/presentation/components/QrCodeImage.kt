package com.pelvictrainer.auth.presentation.components

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import qrcode.QRCode

/**
 * Composable для отображения QR-кода из строки (обычно otpauth:// URI).
 * Генерирует изображение на лету через библиотеку qrcode-kotlin.
 */
@Composable
fun QrCodeImage(
    data: String,
    modifier: Modifier = Modifier,
) {
    val qrBitmap = remember(data) {
        try {
            // Генерируем QR-код
            val qr = QRCode.ofSquares()
                .withSize(12)
                .build(data)

            // Рендерим в PNG байты (без параметров - PNG по умолчанию)
            val pngBytes = qr.render().getBytes()

            // Декодируем в Android Bitmap
            val bitmap = BitmapFactory.decodeByteArray(pngBytes, 0, pngBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            Log.e("QrCodeImage", "Ошибка генерации QR: ${e.message}", e)
            null
        }
    }

    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (qrBitmap != null) {
            Image(
                painter = BitmapPainter(qrBitmap),
                contentDescription = "QR-код для настройки 2FA",
                modifier = Modifier.size(240.dp)
            )
        } else {
            Text(
                text = "QR недоступен",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}