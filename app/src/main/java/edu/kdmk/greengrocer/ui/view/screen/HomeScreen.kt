package edu.kdmk.greengrocer.ui.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import edu.kdmk.greengrocer.data.repository.UserRepository
import edu.kdmk.greengrocer.ui.viewmodel.UserViewModel

@Composable
fun HomeScreen() {
//    LaunchedEffect(Unit) {
//        viewModel.getUsers()  // Ładujemy użytkowników podczas inicjalizacji
//    }
//
//    // Wyświetlamy listę użytkowników
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Home Screen",
//            fontSize = 40.sp,
//            color = Color.Black
//        )
//
//        LazyColumn {
//            items(viewModel.users) { user ->
//                Text(
//                    text = user.fname + " " + user.phone,
//                    fontSize = 20.sp,
//                    color = Color.Black
//                )
//            }
//        }
//    }
}
