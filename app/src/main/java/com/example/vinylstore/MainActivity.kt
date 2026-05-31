package com.example.vinylstore.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vinylstore.ui.screens.CartScreen
import com.example.vinylstore.ui.screens.FeedScreen
import com.example.vinylstore.ui.screens.LoginScreen
import com.example.vinylstore.ui.screens.OrderDetailScreen
import com.example.vinylstore.ui.screens.ProductDetailScreen
import com.example.vinylstore.ui.screens.ProfileScreen
import com.example.vinylstore.ui.screens.SearchScreen
import com.example.vinylstore.ui.screens.WheelScreen
import com.example.vinylstore.ui.screens.admin.AdminOrdersScreen
import com.example.vinylstore.ui.screens.admin.AdminScreen
import com.example.vinylstore.ui.theme.VinylStoreTheme
import com.example.vinylstore.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VinylStoreTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = viewModel()
                MainScreen(navController = navController, viewModel = viewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Feed : Screen("feed", "Лента", Icons.Default.Home)
    data object Search : Screen("search", "Поиск", Icons.Default.Search)
    data object Wheel : Screen("wheel", "Удача", Icons.Default.Star)
    data object Cart : Screen("cart", "Корзина", Icons.Default.ShoppingCart)
    data object Profile : Screen("profile", "Профиль", Icons.Default.Person)
}

@Composable
fun MainScreen(navController: NavHostController, viewModel: MainViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            val currentRoute = currentDestination?.route
            val hideBarRoutes = listOf("login", "product_detail/", "order_detail/", "admin", "admin_orders")
            val showBar = hideBarRoutes.none { currentRoute?.startsWith(it) == true }

            if (showBar) {
                NavigationBar {
                    val items = listOf(Screen.Feed, Screen.Search, Screen.Wheel, Screen.Cart, Screen.Profile)
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feed.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Feed.route) { FeedScreen(navController, viewModel) }
            composable(Screen.Search.route) { SearchScreen(navController, viewModel) }
            composable(Screen.Wheel.route) { WheelScreen(navController, viewModel) }
            composable(Screen.Cart.route) { CartScreen(navController, viewModel) }
            composable(Screen.Profile.route) { ProfileScreen(navController, viewModel) }
            composable("login") { LoginScreen(navController, viewModel) }
            composable("product_detail/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(productId, navController, viewModel)
            }
            composable("order_detail/{orderId}") { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderDetailScreen(orderId, navController, viewModel)
            }
            composable("admin") { AdminScreen(navController, viewModel) }
            composable("admin_orders") { AdminOrdersScreen(navController, viewModel) }
        }
    }
}