package com.example.vinylstore.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.vinylstore.data.model.Product
import com.example.vinylstore.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WheelScreen(navController: NavHostController, viewModel: MainViewModel) {
    val products by viewModel.products.collectAsState()
    val scope = rememberCoroutineScope()

    val wheelItems = remember(products) {
        if (products.size >= 8) products.shuffled().take(8) else products
    }

    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val sectorColors = listOf(
        Color(0xFF8B1A2D), Color(0xFF722F37), Color(0xFFA52A3A), Color(0xFF5D4037),
        Color(0xFFC73E4E), Color(0xFF3E2723), Color(0xFFD4A5A5), Color(0xFF8D6E63),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("\uD83C\uDFB0 Колесо фортуны") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "\uD83C\uDFB2 Испытай удачу!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Крути колесо и выигрывай случайную пластинку со скидкой",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(320.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFD4AF37), Color(0xFFB87333), Color(0xFF8B4513))
                        ),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 12f)
                    )
                }

                Canvas(
                    modifier = Modifier
                        .size(300.dp)
                        .rotate(rotation.value)
                ) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radius = size.minDimension / 2
                    val sectorAngle = 360f / wheelItems.size

                    wheelItems.forEachIndexed { index, _ ->
                        val startAngle = index * sectorAngle - 90f
                        drawArc(
                            color = sectorColors[index % sectorColors.size],
                            startAngle = startAngle,
                            sweepAngle = sectorAngle,
                            useCenter = true,
                            topLeft = Offset(centerX - radius, centerY - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                        val lineAngle = Math.toRadians((startAngle).toDouble())
                        drawLine(
                            color = Color(0xFFD4AF37),
                            start = Offset(centerX, centerY),
                            end = Offset(
                                (centerX + radius * cos(lineAngle)).toFloat(),
                                (centerY + radius * sin(lineAngle)).toFloat()
                            ),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                    drawCircle(color = Color(0xFF3E2723), radius = radius * 0.15f)
                    drawCircle(
                        color = Color(0xFFD4AF37),
                        radius = radius * 0.15f,
                        style = Stroke(width = 4f)
                    )
                }

                Canvas(modifier = Modifier.size(320.dp)) {
                    val centerX = size.width / 2
                    val topY = 10f
                    val trianglePath = Path().apply {
                        moveTo(centerX, topY)
                        lineTo(centerX - 20f, topY + 40f)
                        lineTo(centerX + 20f, topY + 40f)
                        close()
                    }
                    drawPath(
                        path = trianglePath,
                        color = Color(0xFFD4AF37)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (!isSpinning && wheelItems.isNotEmpty()) {
                        isSpinning = true
                        showResult = false
                        val randomIndex = Random.nextInt(wheelItems.size)
                        val sectorAngle = 360f / wheelItems.size
                        val targetRotation = 360f * 5 + randomIndex * sectorAngle + Random.nextFloat() * sectorAngle
                        scope.launch {
                            rotation.animateTo(
                                targetValue = rotation.value + targetRotation,
                                animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing)
                            )
                            selectedProduct = wheelItems[randomIndex]
                            isSpinning = false
                            showResult = true
                        }
                    }
                },
                enabled = !isSpinning && wheelItems.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = CircleShape
            ) {
                Text(
                    text = if (isSpinning) "\uD83C\uDFB0 Крутится..." else "\uD83C\uDFAF Крутить колесо!",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (wheelItems.isEmpty()) {
                Text(
                    text = "\u23F3 Загрузка пластинок...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }

    if (showResult && selectedProduct != null) {
        val product = selectedProduct!!
        val discount = Random.nextInt(10, 51)
        val firstImage = product.imageUrls.firstOrNull() ?: ""

        AlertDialog(
            onDismissRequest = { showResult = false },
            icon = { Text("\uD83C\uDF89", fontSize = 48.sp) },
            title = { Text("Поздравляем!", color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = firstImage,
                        contentDescription = product.name,
                        modifier = Modifier
                            .size(150.dp)
                            .padding(8.dp)
                    )
                    Text(
                        text = product.artist,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "\u00AB${product.name}\u00BB",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${product.year} \u00B7 ${product.format}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFD4AF37), Color(0xFFFFD700))
                                ),
                                shape = CircleShape
                            )
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Скидка $discount%!",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF3E2723)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val newPrice = (product.price * (100 - discount) / 100).toInt()
                    Text(
                        text = "\uD83D\uDCB0 Новая цена: $newPrice \u20BD",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Вместо ${product.price.toInt()} \u20BD",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResult = false
                        viewModel.addToCart(product)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("\uD83D\uDED2 Добавить в корзину со скидкой")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showResult = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("\u274C Закрыть")
                }
            }
        )
    }
}