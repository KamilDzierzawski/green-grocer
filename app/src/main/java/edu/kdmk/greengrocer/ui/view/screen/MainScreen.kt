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
import androidx.navigation.compose.rememberNavController
import edu.kdmk.greengrocer.ui.view.navigation.BottomNavigationBar
import edu.kdmk.greengrocer.ui.view.navigation.NavigationItem

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Używamy remember i mutableStateOf do kontrolowania stanu wybranej zakładki
    val selectedRoute = remember { mutableStateOf(NavigationItem.Home.route) }  // Tworzymy stan dla wybranej zakładki

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                selectedRoute = selectedRoute.value,  // Przekazujemy stan wybranej zakładki
                onItemSelected = { item ->
                    // Zmieniamy stan w navController przy wybraniu nowej zakładki
                    selectedRoute.value = item.route  // Zmiana stanu
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationRoute ?: item.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    // Logowanie po kliknięciu
                    Log.d("MainScreen", "Selected route: ${item.route}")
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Home.route) { HomeScreen() }
            composable(NavigationItem.Search.route) { SearchScreen() }
            composable(NavigationItem.AddPost.route) { AddPostScreen() }
            composable(NavigationItem.Profile.route) { ProfileScreen() }
        }
    }
}