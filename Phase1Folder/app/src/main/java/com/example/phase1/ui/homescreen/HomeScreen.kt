/**
 * Home screen for browsing, opening, importing, sharing, and deleting saved images.
 * Fall 2025 – CS4530
 * Eric Nguyen, Jacob Nguyen, Collin Giles
 */

package com.example.phase1.ui.homescreen

import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.phase1.data.local.ImageRecord
import com.example.phase1.vm.HomeViewModel
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {

    // Screen configuration
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    // Firebase related variables
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var user by remember { mutableStateOf(viewModel.getUser()) }
    // Import screen
    var showImportScreen by remember { mutableStateOf(false) }
    // Share Screen
    var shareScreen by remember { mutableStateOf(false) }
    var sharedImageRecord by remember { mutableStateOf<ImageRecord?>(null) }

    val allImageRecords by viewModel.images.collectAsStateWithLifecycle()

    if (user == null) {
        Column(Modifier.padding(15.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = password, onValueChange = { password = it },
                label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = {
                    scope.launch {
                        error = viewModel.login(email, password)
                        user = viewModel.getUser()
                    }
                }, modifier = Modifier.width(120.dp)) { Text("Login") }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    scope.launch {
                        error = viewModel.createUser(email, password)
                        user = viewModel.getUser()
                    }
                }, modifier = Modifier.width(120.dp) ) { Text("Sign Up") }
            }

            error?.let { Text(it, color = Color.Red) }
        }
        // after user logs in
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val itemWidth = screenWidth - 24
            val itemHeight = screenHeight - 24

            if(showImportScreen) {
                ImportScreen(navController, onDismiss = { showImportScreen = false })
            }
            if(shareScreen && sharedImageRecord != null)
            {
                ShareScreen(navController, onDismiss = { shareScreen = false }, imageRecord = sharedImageRecord!!)
            }

            // Landscape layout
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

                Spacer(modifier = Modifier.height(32.dp))
                if(user != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp, 0.dp))
                    {
                        Text("Welcome, ${user?.email}", modifier = Modifier.padding(5.dp, 0.dp))
                        Button( onClick = { scope.launch {
                            error = viewModel.logout()
                            user = viewModel.getUser()
                        } } )
                        {
                            Text("Logout")
                        }
                    }
                }

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
                                    onClick = { showImportScreen = true },
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
                                                sharedImageRecord = imgRecord
                                                shareScreen = true
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

                Spacer(modifier = Modifier.height(17.dp))
                if(user != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp, 0.dp))
                    {
                        Text("Welcome, ${user?.email}", modifier = Modifier.padding(5.dp, 0.dp))
                        Button( onClick = { scope.launch {
                            error = viewModel.logout()
                            user = viewModel.getUser()
                        } } )
                        {
                            Text("Logout")
                        }
                    }
                }
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
                                    onClick = { showImportScreen = true },
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
                                            sharedImageRecord = imgRecord
                                            shareScreen = true
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
}
