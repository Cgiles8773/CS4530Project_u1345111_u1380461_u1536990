package com.example.phase1.ui.communityscreen

import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.phase1.vm.DrawingViewModel
import com.example.phase1.vm.HomeViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

private data class Drawing(
    val uri: Uri,
    val title: String,
    val userID: String,
)

@Composable
fun CommunityScreen(viewModel: HomeViewModel, drawingViewModel: DrawingViewModel, navController: NavController) {
    Log.d("CommunityScreen","Checkpoint 1")
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val itemWidth = screenWidth - 24
    val itemHeight = screenHeight - 24
    val context = LocalContext.current
    val storage = Firebase.storage
    val db = Firebase.firestore

    Log.d("CommunityScreen","Checkpoint 2")
    Log.d("PackageCheck", context.packageName)




    val user = viewModel.getUser()

    // redirect if invalid
    if (user == null) {
        navController.navigate("login")
        return
    }

    val userId = user.uid

    Log.d("CommunityScreen","Checkpoint 3")

    // Testing starts


    var dataString by remember { mutableStateOf("") }
    LaunchedEffect(user) {
        try {
            val snapshot = db.collection("user_drawings").get().await()
            val doc = snapshot.firstOrNull()
            dataString = doc?.data?.toString() ?: "No data found"
        }
        catch (e: Exception) {
            Log.e("Firestore", "Error fetching", e)
        }
    }

    Column {
        Text("Data string: $dataString")
    }
    // Testing ends


    val communityDrawings by produceState(initialValue = emptyList(), userId) {
        Log.d("CommunityScreen","Checkpoint 4")

        val communityDrawingDocs = viewModel.getAllDocuments()
        val drawings = mutableListOf<Drawing>()
        Log.d("CommunityScreen","Checkpoint 5")

        communityDrawingDocs?.forEach { doc ->
            val title = doc.getString("title") ?: "Untitled"
            val userID = doc.getString("userID") ?: "Unknown"
            val imageUrl = doc.getString("URL")
            // TODO: figure out timestamp implementation here
            // TODO: figure out how to get author from userID

            if (imageUrl != null) {
                try {
                    val uri = Uri.parse(imageUrl)
                    Log.d("CommunityScreen", "$imageUrl, $title, $userID")

                    drawings.add(Drawing(uri, title, userID))
                } catch (e: Exception) {
                    Log.e("CommunityScreen", "Community image conversion failed: ${e.message}")
                }
            } else {
                Log.e("CommunityScreen", "Image URL missing for doc: ${doc.id}")
            }
        }

        value = drawings
        Log.d("CommunityScreen","Checkpoint 6")

    }

    Log.d("CommunityScreen","Checkpoint 7")

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Landscape layout
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LazyRow(
                modifier = Modifier.fillMaxSize()
            ) {
                items(communityDrawings, key = { it.uri.toString() }) { drawing ->
                    Card(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = drawing.uri,
                                contentDescription = drawing.title,
                                modifier = Modifier
                                    .height(150.dp)
                                    .width(150.dp),
                                contentScale = ContentScale.Crop
                            )

                            Column(
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.padding(start = 10.dp)
                            ) {
                                Text(text = drawing.title, style = MaterialTheme.typography.titleMedium)
                                Text(text = "by ${drawing.userID}", style = MaterialTheme.typography.bodySmall)
//                                drawing.timestamp1?.let {
//                                    Text(text = "Uploaded: $it", style = MaterialTheme.typography.bodySmall)
//                                }
                                Button(onClick = {
                                    drawingViewModel.saveImageAsCopy(drawing.title, 1000, 1000)
                                    navController.navigate("main/$")
                                }) {
                                    Text("Copy")
                                }
                            }
                        }
                    }
                }
            }

        }
        // Portrait layout
        else if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(communityDrawings, key = { it.uri.toString() }) { drawing ->
                    Card(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = drawing.uri,
                                contentDescription = drawing.title,
                                modifier = Modifier
                                    .height(150.dp)
                                    .width(150.dp),
                                contentScale = ContentScale.Crop
                            )

                            Column(
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.padding(start = 10.dp)
                            ) {
                                Text(text = drawing.title, style = MaterialTheme.typography.titleMedium)
                                Text(text = "by ${drawing.userID}", style = MaterialTheme.typography.bodySmall)
                                // TODO: Figure out timestamp implementation
//                                drawing.timestamp.let {
//                                    Text(text = "Uploaded: $it.", style = MaterialTheme.typography.bodySmall)
//                                }
                                Button(onClick = {
                                    drawingViewModel.saveImageAsCopy(drawing.title, 1000, 1000)
                                    navController.navigate("main/$")
                                }) {
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