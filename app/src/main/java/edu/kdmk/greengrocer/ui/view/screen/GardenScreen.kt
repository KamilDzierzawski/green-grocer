package edu.kdmk.greengrocer.ui.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.kdmk.greengrocer.ui.view.navigation.NavigationItem

@Composable
fun GardenScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Text(
            text = "Garden",
            fontSize = 40.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )

        FloatingActionButton(
            onClick = {
                navController.navigate("addGardenItem")
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            content = {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Garden Item"
                )
            }
        )
    }
}

@Composable
fun AddGardenItemScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() }
                        //.background(Color.LightGray.copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(8.dp)) // Zaokrąglone rogi
                        .padding(8.dp), // Dodatkowe padding, żeby ikona i tekst miały odpowiednią przestrzeń
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        //tint = Color.Black,
                        modifier = Modifier.padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Back",
                        modifier = Modifier.padding(4.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { navController.popBackStack() }
                        //.background(Color.LightGray.copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(8.dp)) // Zaokrąglone rogi
                        .padding(8.dp), // Dodatkowe padding
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Save",
                        //tint = Color.Black,
                        modifier = Modifier.padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Save",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Add your garden item details here",
                fontSize = 20.sp,
                color = Color.Black
            )
        }

    }
}