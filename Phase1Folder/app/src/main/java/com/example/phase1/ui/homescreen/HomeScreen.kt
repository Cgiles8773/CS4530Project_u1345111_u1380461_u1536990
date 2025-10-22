package com.example.phase1.ui.homescreen

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.phase1.vm.HomeViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val allImageRecords by viewModel.images.collectAsStateWithLifecycle()

    // Launcher for file picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val encodedUri = URLEncoder.encode(uri.toString(), StandardCharsets.UTF_8.toString())
            navController.navigate("main/$encodedUri")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        LazyRow(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(modifier = Modifier.width(220.dp).height(220.dp).padding(vertical = 4.dp))
                {
                    Button(
                        onClick = { navController.navigate("main") },
                        modifier = Modifier.align(Alignment.CenterHorizontally).width(210.dp).height(210.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("New Drawing")
                    }
                }
            }
            item {
                Card(modifier = Modifier.width(220.dp).height(220.dp).padding(vertical = 4.dp))
                {
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.align(Alignment.CenterHorizontally).width(210.dp).height(210.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Import Drawing")
                    }
                }
            }
            items(allImageRecords) { imgRecord ->
                Card(
                    modifier = Modifier
                        .width(220.dp)
                        .height(260.dp)
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Load bitmap once per record
                        val bitmap by produceState<Bitmap?>(initialValue = null, imgRecord) {
                            value = viewModel.loadBitmap(imgRecord)
                        }

                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap!!.asImageBitmap(),
                                contentDescription = imgRecord.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            )
                        } else {
                            Text("Image not found", modifier = Modifier.padding(8.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = imgRecord.name)

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            // Navigate to MainScreen, with file path as argument
                            val encodedPath = URLEncoder.encode(imgRecord.filePath, StandardCharsets.UTF_8.toString())
                            navController.navigate("main/${encodedPath}")
                        }) {
                            Text("Open")
                        }
                    }
                }
            }
        }
    }
}
