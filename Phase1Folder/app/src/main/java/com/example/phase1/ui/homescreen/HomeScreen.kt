/**
 * Home screen for browsing, opening, importing, sharing, and deleting saved images.
 * Fall 2025 – CS4530
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 */

package com.example.phase1.ui.homescreen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.phase1.vm.HomeViewModel
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {

    // Screen configuration
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    val context = LocalContext.current
    val allImageRecords by viewModel.images.collectAsStateWithLifecycle()

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val encoded = URLEncoder.encode(it.toString(), StandardCharsets.UTF_8.toString())
            navController.navigate("main/$encoded")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        val itemWidth = screenWidth - 24
        val itemHeight = screenHeight - 24

        // Landscape layout
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // New + Import Column
                item {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Card(
                            modifier = Modifier
                                .width(250.dp)
                                .height((itemHeight.dp / 2) - 20.dp)
                                .padding(4.dp)
                        ) {
                            Button(
                                onClick = { navController.navigate("main") },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .width(250.dp)
                                    .height((itemHeight - 10).dp),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text("New Drawing") }
                        }

                        Card(
                            modifier = Modifier
                                .width(250.dp)
                                .height((itemHeight.dp / 2) - 20.dp)
                                .padding(4.dp)
                        ) {
                            Button(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .width(250.dp)
                                    .height((itemHeight - 10).dp),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text("Import Image") }
                        }
                    }
                }

                // Saved images
                items(allImageRecords) { imgRecord ->
                    val bitmap by produceState<Bitmap?>(initialValue = null, imgRecord) {
                        value = viewModel.loadBitmap(imgRecord)
                    }

                    if (bitmap == null) {
                        Log.d("HomeScreen", "Bitmap is null for ${imgRecord.name}")
                    } else {
                        Card(
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Image preview
                                if (bitmap != null) {
                                    Image(
                                        bitmap = bitmap!!.asImageBitmap(),
                                        contentDescription = imgRecord.name,
                                        contentScale = ContentScale.Inside,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Column(
                                    verticalArrangement = Arrangement.SpaceEvenly,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(start = 10.dp)
                                ) {
                                    Text(text = imgRecord.name)

                                    Button(
                                        modifier = Modifier.padding(2.dp).width(100.dp),
                                        onClick = {
                                            val encoded = URLEncoder.encode(
                                                imgRecord.filePath,
                                                StandardCharsets.UTF_8.toString()
                                            )
                                            navController.navigate("main/$encoded")
                                        }
                                    ) { Text("Open") }

                                    Button(
                                        modifier = Modifier.padding(2.dp).width(100.dp),
                                        onClick = {
                                            Log.d("ShareDebug", "Path: ${imgRecord.filePath}")
                                            shareImage(context, imgRecord.filePath)
                                        }
                                    ) { Text("Share") }

                                    Button(
                                        modifier = Modifier.padding(2.dp).width(100.dp),
                                        onClick = { viewModel.deleteImage(imgRecord) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                    ) { Text("Delete") }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Portrait layout
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row {
                        Card(
                            modifier = Modifier
                                .width(itemWidth.dp / 2)
                                .height(itemWidth.dp / 2)
                                .padding(4.dp)
                        ) {
                            Button(
                                onClick = { navController.navigate("main") },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .width((itemWidth - 10).dp)
                                    .height(250.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text("New Drawing") }
                        }

                        Card(
                            modifier = Modifier
                                .width(itemWidth.dp / 2)
                                .height(itemWidth.dp / 2)
                                .padding(4.dp)
                        ) {
                            Button(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .width((itemWidth - 10).dp)
                                    .height(250.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text("Import Image") }
                        }
                    }
                }

                // Saved images portrait list
                items(allImageRecords) { imgRecord ->
                    val bitmap by produceState<Bitmap?>(initialValue = null, imgRecord) {
                        value = viewModel.loadBitmap(imgRecord)
                    }

                    Card(
                        modifier = Modifier
                            .width(itemWidth.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap!!.asImageBitmap(),
                                    contentDescription = imgRecord.name,
                                    contentScale = ContentScale.Inside,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = imgRecord.name)

                            Spacer(modifier = Modifier.height(8.dp))

                            Row {
                                Button(
                                    modifier = Modifier.padding(2.dp),
                                    onClick = {
                                        val encoded = URLEncoder.encode(
                                            imgRecord.filePath,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        navController.navigate("main/$encoded")
                                    }
                                ) { Text("Open") }

                                Button(
                                    modifier = Modifier.padding(2.dp),
                                    onClick = {
                                        Log.d("ShareDebug", "Path: ${imgRecord.filePath}")
                                        shareImage(context, imgRecord.filePath)
                                    }
                                ) { Text("Share") }

                                Button(
                                    modifier = Modifier.padding(2.dp),
                                    onClick = { viewModel.deleteImage(imgRecord) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) { Text("Delete") }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Share an image using FileProvider.
 */
fun shareImage(context: Context, imagePath: String) {
    val file = File(imagePath)
    if (!file.exists()) return

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(intent, "Share Image")
    val resInfoList = context.packageManager.queryIntentActivities(chooser, 0)

    resInfoList.forEach {
        context.grantUriPermission(
            it.activityInfo.packageName,
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    context.startActivity(chooser)
}
