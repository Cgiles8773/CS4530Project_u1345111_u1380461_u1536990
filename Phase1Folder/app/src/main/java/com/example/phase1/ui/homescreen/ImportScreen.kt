package com.example.phase1.ui.homescreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ImportScreen(navController: NavController, onDismiss: () -> Unit)
{
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
            { Text("Import device images") }
            Button(onClick = { navController.navigate("community")})
            { Text("Browse community images")}
            Button(onClick = { onDismiss() })
            { Text("Close") }
        }
    }
}