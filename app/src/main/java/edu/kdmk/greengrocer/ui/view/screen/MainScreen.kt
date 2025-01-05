package edu.kdmk.greengrocer.ui.view.screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.kdmk.greengrocer.ui.view.navigation.BottomNavigationBar
import edu.kdmk.greengrocer.ui.view.navigation.NavigationItem

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val isLoggedIn = remember { mutableStateOf(true) }

    val noBottomBarRoutes = listOf("addGardenItem", "editGardenItem")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isLoggedIn.value) {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                if (currentRoute !in noBottomBarRoutes) {
                    BottomNavigationBar(
                        selectedRoute = currentRoute ?: NavigationItem.Home.route,
                        onItemSelected = { item ->
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationRoute ?: item.route) {
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
    ) { innerPadding ->
        if (isLoggedIn.value) {
            NavHost(
                navController = navController,
                startDestination = NavigationItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(NavigationItem.Home.route) { HomeScreen() }
                composable(NavigationItem.Search.route) { SearchScreen() }
                composable(NavigationItem.Garden.route) { GardenScreen(navController) }
                composable(NavigationItem.Profile.route) {
                    ProfileScreen(
                        onLogout = {
                            isLoggedIn.value = false
                        }
                    )
                }
                composable("addGardenItem") { AddGardenItemScreen(navController) }
            }
        } else {
            AuthScreen()
        }
    }
}