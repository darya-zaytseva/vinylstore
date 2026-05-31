package com.example.vinylstore.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.vinylstore.ui.components.FormatEmoji
import com.example.vinylstore.ui.components.SpinningVinylIcon
import com.example.vinylstore.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val products by viewModel.products.collectAsState()
    val product = products.find { it.id == productId }
    val context = LocalContext.current
    var selectedImageIndex by remember { mutableIntStateOf(0) }
    var showFullScreenImage by remember { mutableStateOf(false) }

    if (product == null) {
        Text("Загрузка...", modifier = Modifier.fillMaxSize())
        return
    }

    val images = product.imageUrls
    val currentImage = images.getOrNull(selectedImageIndex) ?: images.firstOrNull() ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.artist) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = FormatEmoji(product.format),
                        fontSize = 48.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    AsyncImage(
                        model = currentImage,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { showFullScreenImage = true },
                        contentScale = ContentScale.Crop
                    )

                    if (images.size > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            images.forEachIndexed { index, url ->
                                val isSelected = index == selectedImageIndex
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else Color.Transparent
                                        )
                                        .clickable { selectedImageIndex = index }
                                        .padding(if (isSelected) 2.dp else 0.dp)
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Фото ${index + 1}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        SpinningVinylIcon(modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = "${product.format} · ${product.condition}",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "${product.artist} · ${product.genre}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Год: ${product.year}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Формат: ${product.format}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Состояние: ${product.condition}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${product.price.toInt()} ₽",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Описание",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val query = Uri.encode("${product.artist} ${product.name}")
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://music.yandex.ru/search?text=$query")
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🎧 Слушать на Яндекс Музыке")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.addToCart(product) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🛒 Добавить в корзину")
                }
            }
        }
    }

    if (showFullScreenImage) {
        Dialog(
            onDismissRequest = { showFullScreenImage = false }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { showFullScreenImage = false }
            ) {
                AsyncImage(
                    model = currentImage,
                    contentDescription = "Полноэкранное фото",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                if (images.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                selectedImageIndex = if (selectedImageIndex > 0)
                                    selectedImageIndex - 1 else images.size - 1
                            }
                        ) {
                            Text("◀", color = Color.White, fontSize = 24.sp)
                        }
                        Text(
                            text = "${selectedImageIndex + 1} / ${images.size}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(
                            onClick = {
                                selectedImageIndex = if (selectedImageIndex < images.size - 1)
                                    selectedImageIndex + 1 else 0
                            }
                        ) {
                            Text("▶", color = Color.White, fontSize = 24.sp)
                        }
                    }
                }

                IconButton(
                    onClick = { showFullScreenImage = false },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text("✕", color = Color.White, fontSize = 24.sp)
                }
            }
        }
    }
}