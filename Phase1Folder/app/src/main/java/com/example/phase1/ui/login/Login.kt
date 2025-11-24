package com.example.phase1.ui.login

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.phase1.vm.LoginViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val storage = Firebase.storage
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf(auth.currentUser) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dataString by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var uploaded by remember { mutableStateOf<String?>(null) }

    //TODO: Remove
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val encoded = URLEncoder.encode(it.toString(), StandardCharsets.UTF_8.toString())
            val storageReference = storage.getReference(encoded)
            val uploadTask = storageReference.putFile(it)
            uploadTask.addOnSuccessListener {
                uploaded = "Uploaded to: ${storageReference.downloadUrl}"
            }
        }
    }

    Column(Modifier.padding(15.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
            )
        if (viewModel.getUser() == null) {
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

            // after user logs in
        } else {
            Text("Welcome, ${user?.email}")
            Text("Id: ${user?.uid}")
            Button(onClick = {
                scope.launch {
                    error = viewModel.logout()
                    user = viewModel.getUser()
                }
            }) { Text("Logout") }

            //db.
            Button( onClick = { navController.navigate("main") } )
            { Text("New Drawing") }

            Button( onClick = {imagePickerLauncher.launch("image/*")})
            {Text("Import Image")}
            uploaded?.let {
                Text(it)
            }
            val imageRef = storage.reference.child("apple.png")

            // State to hold the image URI
            var imageUri by remember { mutableStateOf<Uri?>(null) }

            // Use LaunchedEffect to fetch the download URL asynchronously
            LaunchedEffect(imageRef) {
                try {
                    imageUri = imageRef.downloadUrl.await()
                } catch (e: Exception) {
                    error = "Image download failed: ${e.message}"
                }
            }

            // Display the image using the Coil library once the URI is available
            imageUri?.let {
                AsyncImage(
                    model = it.toString(),
                    contentDescription = "Apple from Firebase Storage",
                    modifier = Modifier.height(150.dp)
                )
            }
        }
    }
}
