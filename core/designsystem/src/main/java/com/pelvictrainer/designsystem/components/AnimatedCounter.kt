package com.pelvictrainer.designsystem.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Анимированный счётчик, который плавно меняет цифры при изменении значения.
 * Использует slide + fade анимацию для эффекта "прокрутки" цифр.
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineSmall,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight? = FontWeight.Bold,
    animationDurationMs: Int = 400,
    prefix: String = "",
    suffix: String = ""
) {
    AnimatedContent(
        targetState = targetValue,
        transitionSpec = {
            if (targetState > initialState) {
                // Увеличение: новая цифра приходит снизу, старая уходит вверх
                (slideInVertically(
                    animationSpec = tween(durationMillis = animationDurationMs),
                    initialOffsetY = { it / 2 }
                ) + fadeIn(animationSpec = tween(durationMillis = animationDurationMs)))
                    .togetherWith(
                        slideOutVertically(
                            animationSpec = tween(durationMillis = animationDurationMs),
                            targetOffsetY = { -it / 2 }
                        ) + fadeOut(animationSpec = tween(durationMillis = animationDurationMs))
                    )
            } else {
                // Уменьшение: новая цифра приходит сверху, старая уходит вниз
                (slideInVertically(
                    animationSpec = tween(durationMillis = animationDurationMs),
                    initialOffsetY = { -it / 2 }
                ) + fadeIn(animationSpec = tween(durationMillis = animationDurationMs)))
                    .togetherWith(
                        slideOutVertically(
                            animationSpec = tween(durationMillis = animationDurationMs),
                            targetOffsetY = { it / 2 }
                        ) + fadeOut(animationSpec = tween(durationMillis = animationDurationMs))
                    )
            }
        },
        label = "AnimatedCounter"
    ) { value ->
        Text(
            text = "$prefix$value$suffix",
            modifier = modifier,
            style = style,
            color = color,
            fontWeight = fontWeight
        )
    }
}

/**
 * Анимированный счётчик для Long значений (например, длительность в секундах).
 */
@Composable
fun AnimatedCounterLong(
    targetValue: Long,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineSmall,
    color: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight? = FontWeight.Bold,
    animationDurationMs: Int = 400,
    prefix: String = "",
    suffix: String = ""
) {
    AnimatedContent(
        targetState = targetValue,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically(
                    animationSpec = tween(durationMillis = animationDurationMs),
                    initialOffsetY = { it / 2 }
                ) + fadeIn(animationSpec = tween(durationMillis = animationDurationMs)))
                    .togetherWith(
                        slideOutVertically(
                            animationSpec = tween(durationMillis = animationDurationMs),
                            targetOffsetY = { -it / 2 }
                        ) + fadeOut(animationSpec = tween(durationMillis = animationDurationMs))
                    )
            } else {
                (slideInVertically(
                    animationSpec = tween(durationMillis = animationDurationMs),
                    initialOffsetY = { -it / 2 }
                ) + fadeIn(animationSpec = tween(durationMillis = animationDurationMs)))
                    .togetherWith(
                        slideOutVertically(
                            animationSpec = tween(durationMillis = animationDurationMs),
                            targetOffsetY = { it / 2 }
                        ) + fadeOut(animationSpec = tween(durationMillis = animationDurationMs))
                    )
            }
        },
        label = "AnimatedCounterLong"
    ) { value ->
        Text(
            text = "$prefix$value$suffix",
            modifier = modifier,
            style = style,
            color = color,
            fontWeight = fontWeight
        )
    }
}