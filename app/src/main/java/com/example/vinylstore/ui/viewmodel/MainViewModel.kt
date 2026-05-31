package com.example.vinylstore.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylstore.data.model.CartItem
import com.example.vinylstore.data.model.Order
import com.example.vinylstore.data.model.Product
import com.example.vinylstore.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    // Auth
    val currentUser = mutableStateOf(repository.getCurrentUser())
    val isAdmin = mutableStateOf(false)
    val authError = mutableStateOf<String?>(null)
    val isLoading = mutableStateOf(false)

    // Data
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _allOrders = MutableStateFlow<List<Order>>(emptyList())
    val allOrders: StateFlow<List<Order>> = _allOrders.asStateFlow()

    init {
        loadProducts()
        loadCart()
        loadOrders()
        checkAdmin()
    }

    // ==================== AUTH ====================
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            authError.value = null
            val result = repository.signIn(email, password)
            result.onSuccess {
                currentUser.value = it
                checkAdmin()
                loadCart()
                loadOrders()
            }.onFailure {
                authError.value = it.message
            }
            isLoading.value = false
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            authError.value = null
            val result = repository.signUp(email, password)
            result.onSuccess {
                currentUser.value = it
            }.onFailure {
                authError.value = it.message
            }
            isLoading.value = false
        }
    }

    fun signOut() {
        repository.signOut()
        currentUser.value = null
        isAdmin.value = false
        _cartItems.value = emptyList()
        _orders.value = emptyList()
        _allOrders.value = emptyList()
    }

    private fun checkAdmin() {
        viewModelScope.launch {
            isAdmin.value = repository.isAdmin()
        }
    }

    // ==================== PRODUCTS ====================
    private fun loadProducts() {
        viewModelScope.launch {
            repository.getProducts().collect {
                _products.value = it
            }
        }
    }

    // ==================== CART ====================
    private fun loadCart() {
        viewModelScope.launch {
            repository.getCart().collect {
                _cartItems.value = it
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val item = CartItem(
                productId = product.id,
                name = product.name,
                artist = product.artist,
                price = product.price,
                imageUrl = product.imageUrls.firstOrNull() ?: "",  // Первое фото
                quantity = 1
            )
            repository.addToCart(item)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    // ==================== ORDERS ====================
    private fun loadOrders() {
        viewModelScope.launch {
            repository.getMyOrders().collect {
                _orders.value = it
            }
        }
    }

    fun createOrder() {
        viewModelScope.launch {
            val items = _cartItems.value
            if (items.isEmpty()) return@launch
            val total = items.sumOf { it.price * it.quantity }
            val order = Order(
                userId = repository.getCurrentUser()?.uid ?: "",
                items = items,
                totalPrice = total,
                status = "Создан"
            )
            repository.createOrder(order)
            items.forEach { repository.removeFromCart(it.productId) }
        }
    }

    // ==================== ADMIN ====================
    suspend fun addProduct(product: Product): Result<String> {
        return repository.addProduct(product)
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    fun loadAllOrders() {
        viewModelScope.launch {
            repository.getAllOrders().collect {
                _allOrders.value = it
            }
        }
    }

    fun updateOrderStatus(orderId: String, status: String, trackNumber: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status, trackNumber)
        }
    }
}