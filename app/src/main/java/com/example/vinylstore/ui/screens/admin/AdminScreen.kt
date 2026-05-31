package com.example.vinylstore.ui.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.vinylstore.data.model.Product
import com.example.vinylstore.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavHostController, viewModel: MainViewModel) {
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("Винил") }
    var isNew by remember { mutableStateOf(false) }
    var imageUrlsInput by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💿 ")
                        Text("Добавить пластинку")
                    }
                },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = artist,
                onValueChange = { artist = it },
                label = { Text("🎤 Исполнитель") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("💿 Название альбома") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("🎵 Жанр") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("📅 Год выпуска") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("💰 Цена") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = format,
                onValueChange = { format = it },
                label = { Text("📼 Формат (Винил / CD / Кассета / Услуга)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = condition,
                onValueChange = { condition = it },
                label = { Text("✨ Состояние (NM / VG+ / ...)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = imageUrlsInput,
                onValueChange = { imageUrlsInput = it },
                label = { Text("🖼️ Ссылки на фото (через запятую или новую строку)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                placeholder = { Text("https://example.com/photo1.jpg, https://example.com/photo2.jpg") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("📝 Описание") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isNew,
                    onCheckedChange = { isNew = it }
                )
                Text(text = "🔥 Новинка")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (statusMessage.isNotEmpty()) {
                Text(
                    text = statusMessage,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (artist.isBlank() || name.isBlank() || price.isBlank() || year.isBlank()) {
                        statusMessage = "Заполните обязательные поля"
                        return@Button
                    }

                    val urls = imageUrlsInput
                        .split(",", "\\n")
                        .map { it.trim() }
                        .filter { it.isNotBlank() && it.startsWith("http") }

                    if (urls.isEmpty()) {
                        statusMessage = "Добавьте хотя бы одну ссылку на фото"
                        return@Button
                    }

                    val product = Product(
                        name = name,
                        artist = artist,
                        genre = genre,
                        year = year.toIntOrNull() ?: 0,
                        price = price.toDoubleOrNull() ?: 0.0,
                        description = description,
                        imageUrls = urls,
                        format = format,
                        condition = condition,
                        isNew = isNew
                    )
                    scope.launch {
                        val result = viewModel.addProduct(product)
                        result.onSuccess {
                            statusMessage = "✅ Товар добавлен!"
                            name = ""
                            artist = ""
                            genre = ""
                            year = ""
                            price = ""
                            description = ""
                            condition = ""
                            imageUrlsInput = ""
                            isNew = false
                        }.onFailure { e ->
                            statusMessage = "❌ Ошибка: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("💾 Сохранить товар")
            }
        }
    }
}