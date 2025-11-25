package com.example.phase1.ui.communityscreen

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.phase1.ui.homescreen.shareImage
import com.example.phase1.vm.DrawingViewModel
import com.example.phase1.vm.HomeViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private data class Drawing(val uri: Uri, val title: String, val author: String, val filePath: String)

@Composable
fun CommunityScreen(viewModel: HomeViewModel, drawingViewModel: DrawingViewModel, navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val itemWidth = screenWidth - 24
    val itemHeight = screenHeight - 24
    val context = LocalContext.current
    val storage = Firebase.storage

    val user = viewModel.getUser()
    if (user == null) {
        navController.navigate("login")
        return
    }
    val userId = user.uid

    val communityDrawings by produceState<List<Drawing>>(initialValue = emptyList(), userId) {
        val communityDrawingDocs = viewModel.getAllDocuments()
        val drawings = mutableListOf<Drawing>()
        communityDrawingDocs?.forEach { doc ->
            val filePath = doc.getString("filePath")
            val title = doc.getString("name") ?: "Untitled"
            val author = doc.getString("authorName") ?: "Unknown"
            if (filePath != null) {
                try {
                    val uri = storage.getReference(filePath).downloadUrl.await()
                    drawings.add(Drawing(uri, title, author, filePath))
                } catch (e: Exception) {
                    Log.e("CommunityScreen", "Community image download failed: ${e.message}")
                }
            }
        }
        value = drawings
    }


    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Landscape layout
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Spacer(modifier = Modifier.height(17.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text(
                    text = "Community Browser",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = { navController.navigate("home") },
                ) { Text("Back Home") }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Community Drawings
                items(communityDrawings, key = { it.filePath }) { drawing ->
                    Card(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = drawing.uri.toString(),
                                contentDescription = drawing.title,
                                modifier = Modifier
                                    .height(150.dp)
                                    .width(150.dp),
                                contentScale = ContentScale.Crop
                            )

                            Column(
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(start = 10.dp)
                            ) {
                                Text(text = drawing.title, style = MaterialTheme.typography.titleMedium)
                                Text(text = "by ${drawing.author}", style = MaterialTheme.typography.bodySmall)
                                Button(onClick = {
                                    //TODO: Save image locally as a copy
                                    drawingViewModel.saveImageAsCopy(drawing.title, 1000, 1000)
                                    navController.navigate("main/$")
                                })
                                {
                                    Text("Copy")
                                }
                            }
                        }
                    }
                }
            }
        }
        // Portrait layout
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Spacer(modifier = Modifier.height(17.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text(
                    text = "Community Browser",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = { navController.navigate("home") },
                ) { Text("Back Home") }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Community Drawings
                items(communityDrawings, key = { it.filePath }) { drawing ->
                    Card(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = drawing.uri.toString(),
                                contentDescription = drawing.title,
                                modifier = Modifier
                                    .height(150.dp)
                                    .width(150.dp),
                                contentScale = ContentScale.Crop
                            )

                            Column(
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(start = 10.dp)
                            ) {
                                Text(text = drawing.title, style = MaterialTheme.typography.titleMedium)
                                Text(text = "by ${drawing.author}", style = MaterialTheme.typography.bodySmall)
                                Button(onClick = {
                                    //TODO: Save image locally as a copy

                                    navController.navigate("main/$")
                                })
                                {
                                    Text("Copy")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}