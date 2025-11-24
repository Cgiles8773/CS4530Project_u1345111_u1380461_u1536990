package com.example.phase1.ui.homescreen

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ShareScreen(navController: NavController, onDismiss: () -> Unit, filepath: String)
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
        Card(modifier = Modifier.padding(24.dp))
        {
            Button(onClick = { imagePickerLauncher.launch("image/*") })
            { Text("Share via device") }
            Button(onClick = { shareImage(context, filepath)})
            { Text("Upload to community")}
            Button(onClick = { onDismiss() })
            { Text("Close") }
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