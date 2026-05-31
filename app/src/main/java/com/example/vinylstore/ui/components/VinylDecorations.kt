package com.example.vinylstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Эмодзи для разных форматов
const val VINYL_EMOJI = "💿"
const val CD_EMOJI = "📀"
const val CASSETTE_EMOJI = "📼"
const val MUSIC_NOTE_EMOJI = "🎵"
const val HEADPHONES_EMOJI = "🎧"
const val MICROPHONE_EMOJI = "🎤"
const val GUITAR_EMOJI = "🎸"
const val SAXOPHONE_EMOJI = "🎷"

@Composable
fun FormatEmoji(format: String): String {
    return when (format) {
        "Винил", "LP", "7\"", "10\"" -> VINYL_EMOJI
        "CD", "Mini-CD" -> CD_EMOJI
        "Кассета" -> CASSETTE_EMOJI
        "Услуга" -> MUSIC_NOTE_EMOJI
        else -> MUSIC_NOTE_EMOJI
    }
}

@Composable
fun VinylBadge(
    text: String,
    modifier: Modifier = Modifier,
    emoji: String = VINYL_EMOJI
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = emoji,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = Color.White,
                style = TextStyle(fontSize = 12.sp)
            )
        }
    }
}

@Composable
fun SpinningVinylIcon(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF333333),
                        Color(0xFF1A1A1A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Центральная метка винила
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(Color(0xFFD4AF37)) // золотая
        )
        // Маленький текст в центре
        Text(
            text = "33⅓",
            color = Color(0xFFD4AF37),
            fontSize = 8.sp
        )
    }
}

@Composable
fun CassetteIcon(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(64.dp, 40.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF8B4513),
                        Color(0xFFA0522D)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = CASSETTE_EMOJI,
            fontSize = 24.sp
        )
    }
}

@Composable
fun GenreIcon(genre: String): String {
    return when (genre) {
        "Rock" -> "🎸"
        "Jazz" -> "🎷"
        "Hip-Hop" -> "🎤"
        "Electronic" -> "🎹"
        "Classical" -> "🎻"
        "Pop" -> "🎵"
        "Soundtrack" -> "🎬"
        else -> MUSIC_NOTE_EMOJI
    }
}