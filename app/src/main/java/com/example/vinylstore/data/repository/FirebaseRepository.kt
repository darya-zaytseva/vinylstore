package com.example.vinylstore.data.repository

import com.example.vinylstore.data.model.CartItem
import com.example.vinylstore.data.model.Order
import com.example.vinylstore.data.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ==================== AUTH ====================
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    fun isLoggedIn(): Boolean = auth.currentUser != null

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    // ==================== PRODUCTS ====================
    fun getProducts(): Flow<List<Product>> = callbackFlow {
        val listener = db.collection("products")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.map { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id) ?: Product()
                } ?: emptyList()
                trySend(products)
            }
        awaitClose { listener.remove() }
    }

    fun getNewProducts(): Flow<List<Product>> = callbackFlow {
        val listener = db.collection("products")
            .whereEqualTo("isNew", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.map { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id) ?: Product()
                } ?: emptyList()
                trySend(products)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addProduct(product: Product): Result<String> {
        return try {
            val docRef = db.collection("products").add(product).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String) {
        db.collection("products").document(productId).delete().await()
    }

    // ==================== CART ====================
    suspend fun addToCart(item: CartItem) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("carts").document(userId)
            .collection("items").document(item.productId)
            .set(item)
            .await()
    }

    suspend fun removeFromCart(productId: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("carts").document(userId)
            .collection("items").document(productId)
            .delete()
            .await()
    }

    fun getCart(): Flow<List<CartItem>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = db.collection("carts").document(userId)
            .collection("items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.map { doc ->
                    doc.toObject(CartItem::class.java)?.copy(productId = doc.id) ?: CartItem()
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    // ==================== ORDERS ====================
    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val docRef = db.collection("orders").add(order).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getMyOrders(): Flow<List<Order>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = db.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.documents?.map { doc ->
                    doc.toObject(Order::class.java)?.copy(id = doc.id) ?: Order()
                } ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    fun getAllOrders(): Flow<List<Order>> = callbackFlow {
        val listener = db.collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.documents?.map { doc ->
                    doc.toObject(Order::class.java)?.copy(id = doc.id) ?: Order()
                } ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateOrderStatus(orderId: String, status: String, trackNumber: String) {
        val updates = hashMapOf<String, Any>(
            "status" to status,
            "trackNumber" to trackNumber
        )
        db.collection("orders").document(orderId).update(updates).await()
    }

    // ==================== ADMIN ====================
    suspend fun isAdmin(): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        return try {
            val doc = db.collection("admins").document(userId).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }
}