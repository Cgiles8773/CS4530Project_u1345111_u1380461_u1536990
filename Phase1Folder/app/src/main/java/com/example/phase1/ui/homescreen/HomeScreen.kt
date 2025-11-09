package com.example.phase1.ui.homescreen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    // Launcher for file picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {

            val encodedUri = URLEncoder.encode(uri.toString(), StandardCharsets.UTF_8.toString())
            navController.navigate("main/$encodedUri")
        }
    }

    // Holds the visible state of drawing options
    var showDrawingOptions by remember { mutableStateOf(false)}

//    if (showDrawingOptions) {
//        drawingOptionsDialog(navController, imagePickerLauncher, { showDrawingOptions = false})
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        val itemWidth = screenWidth - 24

        // TODO: Add LazyRow when landscape mode
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row()
                {
                    Card(modifier = Modifier.width(itemWidth.dp/2).height(itemWidth.dp/2).padding(4.dp))
                    {
                        Button(
                            onClick = { navController.navigate("main") },
                            modifier = Modifier.align(Alignment.CenterHorizontally).width((itemWidth - 10).dp).height(250.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("New Drawing")
                        }
                    }
                    Card(modifier = Modifier.width(itemWidth.dp/2).height(itemWidth.dp/2).padding(4.dp))
                    {
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.align(Alignment.CenterHorizontally).width((itemWidth - 10).dp).height(250.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Import Image")
                        }
                    }
                }
            }

            items(allImageRecords) { imgRecord ->
                // Load bitmap once per record
                val bitmap by produceState<Bitmap?>(initialValue = null, imgRecord) {
                    value = viewModel.loadBitmap(imgRecord)
                }
                if (bitmap == null)
                    Log.d("HomeScreen", "Bitmap is null for ${imgRecord.name}")
                else
                {
                    Card(
                        modifier = Modifier
                            .width(itemWidth.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {


                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap!!.asImageBitmap(),
                                    contentDescription = imgRecord.name,
                                    contentScale = ContentScale.Inside,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                    //.height(140.dp)
                                )
                            } else {
                                Text("Image not found", modifier = Modifier.padding(8.dp))
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = imgRecord.name)

                            Spacer(modifier = Modifier.height(8.dp))
                            Row {

                                // Open
                                Button(
                                    modifier = Modifier.padding(2.dp),
                                    onClick = {
                                        // Navigate to MainScreen, with file path as argument
                                        val encodedPath = URLEncoder.encode(
                                            imgRecord.filePath,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        navController.navigate("main/${encodedPath}")
                                    }) {
                                    Text("Open")
                                }

                                // Share
                                Button(
                                    modifier = Modifier.padding(2.dp),
                                    onClick = {

                                        Log.d("ShareDebug", "Path: ${imgRecord.filePath}")
                                        shareImage(context, imgRecord.filePath)
                                    }) {
                                    Text("Share")
                                }
                                // Delete
                                Button(
                                    modifier = Modifier.padding(2.dp),
                                    onClick = { viewModel.deleteImage(imgRecord) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                )
                                {
                                    Text("Delete")
                                }
                            }

                        }
                    }
            }
            }
        }
    }
}

/**
 * This function provides the UI popup for the user to choose the drawing type
 * Takes in a lambda to handle the closing of dialog
 */
@Composable
fun drawingOptionsDialog(navController: NavController, imagePickerLauncher : ManagedActivityResultLauncher<String, Uri?>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose drawing option")},
        text = {

            // Display the 2 options, new drawing, or open an image
            Column {
                TextButton(
                    onClick = { navController.navigate("main") },
                    ) { Text("New Drawing")}

                TextButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                ) { Text("Import Image")}
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel")}
        },
        confirmButton = {}
    )
}

/**
 * This function provides sharing capabilities for each image
 */
fun shareImage(context: Context, imagePath: String) {
    val file = File(imagePath)
    if (!file.exists()) return

    // Use FileProvider to get a content URI
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    Log.d("ShareDebug", "URI: ${uri}")

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(shareIntent, "Share Image")
    val resInfoList = context.packageManager.queryIntentActivities(chooser, 0)
    for (resolveInfo in resInfoList) {
        context.grantUriPermission(
            resolveInfo.activityInfo.packageName,
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
    context.startActivity(chooser)
}
