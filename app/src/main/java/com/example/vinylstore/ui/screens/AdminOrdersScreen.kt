package com.example.vinylstore.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.vinylstore.data.model.Order
import com.example.vinylstore.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(navController: NavHostController, viewModel: MainViewModel) {
    val allOrders by viewModel.allOrders.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Логистика — все заказы") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
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
        ) {
            if (allOrders.isEmpty()) {
                Text(
                    text = "Пока нет заказов",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(allOrders) { order ->
                        AdminOrderCard(
                            order = order,
                            onStatusChange = { newStatus ->
                                viewModel.updateOrderStatus(order.id, newStatus, order.trackNumber)
                            },
                            onTrackUpdate = { newTrack ->
                                viewModel.updateOrderStatus(order.id, order.status, newTrack)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderCard(
    order: Order,
    onStatusChange: (String) -> Unit,
    onTrackUpdate: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var trackInput by remember { mutableStateOf(order.trackNumber) }
    var showDetails by remember { mutableStateOf(false) }
    val statuses = listOf("Создан", "Комплектуется", "Передан в доставку", "В пункте выдачи", "Доставлен")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .clickable { showDetails = !showDetails }
        ) {
            Text(
                text = "Заказ №${order.id.take(6)}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "Покупатель: ${order.userId.take(8)}...")
            Text(text = "Сумма: ${order.totalPrice.toInt()} ₽")
            Text(
                text = "Текущий статус: ${order.status}",
                color = MaterialTheme.colorScheme.primary
            )
            if (order.trackNumber.isNotBlank()) {
                Text(text = "Трек: ${order.trackNumber}")
            }

            if (showDetails) {
                Spacer(modifier = Modifier.height(8.dp))

                Text("Сменить статус:", style = MaterialTheme.typography.bodyMedium)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = order.status,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statuses.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    onStatusChange(status)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = trackInput,
                    onValueChange = { trackInput = it },
                    label = { Text("Трек-номер") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onTrackUpdate(trackInput) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить трек-номер")
                }
            }
        }
    }
}