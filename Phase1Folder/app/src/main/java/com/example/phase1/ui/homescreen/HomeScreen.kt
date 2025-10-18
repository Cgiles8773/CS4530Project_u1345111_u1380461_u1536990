package com.example.phase1.ui.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.phase1.vm.HomeViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val allImageRecords by viewModel.images.collectAsStateWithLifecycle()

    // Wrap everything in a Column to stack vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // The LazyRow of cards
        LazyRow(
            modifier = Modifier
                .weight(1f) // take available vertical space
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allImageRecords) { imgRecord ->
                Card(
                    modifier = Modifier
                        .width(220.dp)
                        .height(180.dp) // ⬅️ increase card height
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = imgRecord.name)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = imgRecord.filePath)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = imgRecord.date.toString())
                        Spacer(modifier = Modifier.height(8.dp))
                        val bitmap = viewModel.loadBitmap(imgRecord)
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = imgRecord.name,
                                modifier = Modifier
                                    .height(450.dp)
                                    .width(450.dp)
                            )
                        } else {
                            Text("Image not found", modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // The button below the LazyRow
        Button(
            onClick = { navController.navigate("main") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Go to Main Screen")
        }
    }
}
