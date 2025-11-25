package com.example.phase1.ui.homescreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.phase1.data.local.ImageRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ShareScreen(navController: NavController, onDismiss: () -> Unit, imageRecord: ImageRecord)
{
    val context = LocalContext.current

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val encoded = URLEncoder.encode(it.toString(), StandardCharsets.UTF_8.toString())
            navController.navigate("main/$encoded")
        }
    }

    Dialog(onDismissRequest = onDismiss)
    {
        Card()
        {
            Spacer(modifier = Modifier.height(17.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth())
            {
                Text(
                    text = "Share",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(modifier = Modifier.padding(12.dp, 5.dp), onClick = {
                    shareImage(context, imageRecord.filePath)
                    onDismiss()
                })
                { Text("Share via device") }
                Button(modifier = Modifier.padding(12.dp, 5.dp), onClick = {
                    uploadImageToFirebase(context, imageRecord)
                    onDismiss()
                })
                { Text("Upload to community") }
                Button(modifier = Modifier.padding(12.dp, 5.dp), onClick = { onDismiss() })
                { Text("Close") }
            }
        }
    }
}

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

fun uploadImageToFirebase(context: Context, imageRecord: ImageRecord) {
    val file = File(imageRecord.filePath)
    if (!file.exists()) {
        Toast.makeText(context, "File not found!", Toast.LENGTH_SHORT).show()
        return
    }

    val storageRef = FirebaseStorage.getInstance().reference
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    if (userId == null) {
        Toast.makeText(context, "You must be logged in to upload.", Toast.LENGTH_SHORT).show()
        return
    }

    val fileUri = Uri.fromFile(file)
    val imageRef = storageRef.child("images/${file.name}")

    imageRef.putFile(fileUri)
        .addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val drawingData = hashMapOf(
                    "URL" to uri.toString(),
                    "timestamp" to imageRecord.date,
                    "title" to imageRecord.name,
                    "userID" to userId
                )

                firestore.collection("user_drawings")
                    .add(drawingData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Upload successful!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to create database record: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
}
