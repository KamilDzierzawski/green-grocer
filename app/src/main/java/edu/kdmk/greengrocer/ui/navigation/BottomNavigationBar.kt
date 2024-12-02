package edu.kdmk.greengrocer.ui.navigation

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val title: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    data object Home : NavigationItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Search : NavigationItem("search", "Search", Icons.Filled.Search, Icons.Outlined.Search)
    data object AddPost : NavigationItem("addPost", "Add Post", Icons.Filled.Add, Icons.Outlined.Add)
    data object Profile : NavigationItem("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun BottomNavigationBar(
    selectedRoute: String,
    onItemSelected: (NavigationItem) -> Unit
) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Search,
        NavigationItem.AddPost,
        NavigationItem.Profile
    )

    NavigationBar {
        items.forEach { item ->
            val isSelected = item.route == selectedRoute

            NavigationBarItem(
                selected = isSelected, // Sprawdzamy, czy zakładka jest wybrana
                onClick = {
                    // Logowanie kliknięcia na zakładkę i zmiany ikony
                    Log.d("BottomNavigationBar", "Item clicked: ${item.route}")

                    // Dodajemy logowanie przy zmianie ikony:
                    if (isSelected) {
                        Log.d("BottomNavigationBar", "Icon changed: ${item.route} - Using Filled Icon: ${item.icon}")
                    } else {
                        Log.d("BottomNavigationBar", "Icon changed: ${item.route} - Using Outlined Icon: ${item.selectedIcon}")
                    }

                    // Wybór zakładki
                    onItemSelected(item)
                },
                label = { Text(text = item.title) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.icon else item.selectedIcon,
                        contentDescription = item.title
                    )
                }
            )
        }
    }
}
