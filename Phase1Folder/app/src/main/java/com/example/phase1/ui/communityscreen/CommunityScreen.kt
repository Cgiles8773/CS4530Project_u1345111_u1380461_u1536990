package com.example.phase1.ui.communityscreen

import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.phase1.vm.DrawingViewModel
import com.example.phase1.vm.HomeViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class Drawing(
    val uri: Uri,
    val title: String,
    val userID: String,
    val author: String,
    val timestamp: Long? = null
)

@Composable
fun CommunityScreen(
    viewModel: HomeViewModel,
    drawingViewModel: DrawingViewModel,
    navController: NavController
) {
    Log.d("CommunityScreen", "Checkpoint 1")
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val storage = Firebase.storage
    val db = Firebase.firestore

    Log.d("CommunityScreen", "Checkpoint 2")
    Log.d("PackageCheck", context.packageName)

    val user = viewModel.getUser()

    // redirect if invalid
    if (user == null) {
        navController.navigate("login")
        return
    }

    val userId = user.uid
    Log.d("CommunityScreen", "Checkpoint 3")

    val communityDrawings by produceState(initialValue = emptyList<Drawing>(), userId) {
        Log.d("CommunityScreen", "Checkpoint 4")

        val communityDrawingDocs = viewModel.getAllDocuments()
        val drawings = mutableListOf<Drawing>()
        Log.d("CommunityScreen", "Checkpoint 5")

        communityDrawingDocs?.forEach { doc ->
            val title = doc.getString("title") ?: "Untitled"
            val userID = doc.getString("userID") ?: "Unknown"
            val imageUrl = doc.getString("URL")
            val userAuthor = doc.getString("author") ?: "Unknown"
            val timestamp = doc.getLong("timestamp") // <-- NEW

            if (imageUrl != null) {
                try {
                    val uri = Uri.parse(imageUrl)
                    Log.d("CommunityScreen", "$imageUrl, $title, $userID")
                    drawings.add(Drawing(uri, title, userID, userAuthor, timestamp))
                } catch (e: Exception) {
                    Log.e("CommunityScreen", "Community image conversion failed: ${e.message}")
                }
            } else {
                Log.e("CommunityScreen", "Image URL missing for doc: ${doc.id}")
            }
        }

        value = drawings
        Log.d("CommunityScreen", "Checkpoint 6")
    }

    Log.d("CommunityScreen", "Checkpoint 7")
    val scope = rememberCoroutineScope()

    // ---------- LANDSCAPE ----------
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.navigate("home") }) {
                Text("Return home")
            }

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
                                    .height(200.dp)
                                    .width(200.dp),
                                contentScale = ContentScale.Crop
                            )

                            Column(
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.padding(start = 10.dp)
                            ) {
                                Text(
                                    text = drawing.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "by ${drawing.author}",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                // ---- TIMESTAMP ----
                                drawing.timestamp?.let { ts ->
                                    val formatted = SimpleDateFormat(
                                        "MMM dd, yyyy h:mm a",
                                        Locale.getDefault()
                                    ).format(Date(ts))

                                    Text(
                                        text = formatted,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Button(onClick = {
                                    scope.launch {
                                        try {
                                            val loader = ImageLoader(context)
                                            val request = ImageRequest.Builder(context)
                                                .data(drawing.uri.toString())
                                                .allowHardware(false)
                                                .build()

                                            val result = loader.execute(request)
                                            if (result is SuccessResult) {
                                                val bitmap = (result.drawable as BitmapDrawable).bitmap
                                                CommunityImageHolder.bitmap = bitmap
                                                navController.navigate("main")
                                            }
                                        } catch (e: Exception) {
                                            Log.e("CommunityScreen", "Copy failed: ${e.message}")
                                        }
                                    }
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

    // ---------- PORTRAIT ----------
    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { navController.navigate("home") }) { Text("Return home") }
            }

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
                                .height(200.dp)
                                .width(200.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier.padding(start = 10.dp)
                        ) {
                            Text(
                                text = drawing.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "by ${drawing.author}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            // ---- TIMESTAMP ----
                            drawing.timestamp?.let { ts ->
                                val formatted = SimpleDateFormat(
                                    "MMM dd, yyyy h:mm a",
                                    Locale.getDefault()
                                ).format(Date(ts))

                                Text(
                                    text = formatted,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Button(onClick = {
                                scope.launch {
                                    try {
                                        val loader = ImageLoader(context)
                                        val request = ImageRequest.Builder(context)
                                            .data(drawing.uri.toString())
                                            .allowHardware(false)
                                            .build()

                                        val result = loader.execute(request)
                                        if (result is SuccessResult) {
                                            val bitmap = (result.drawable as BitmapDrawable).bitmap
                                            CommunityImageHolder.bitmap = bitmap
                                            navController.navigate("main")
                                        }
                                    } catch (e: Exception) {
                                        Log.e("CommunityScreen", "Copy failed: ${e.message}")
                                    }
                                }
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
