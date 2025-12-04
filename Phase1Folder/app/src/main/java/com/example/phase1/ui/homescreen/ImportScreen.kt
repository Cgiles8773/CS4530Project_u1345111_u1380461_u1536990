package com.example.phase1.ui.homescreen

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
        Card()
        {
            Spacer(modifier = Modifier.height(17.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth())
            {
                Text(
                    text = "Import",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(modifier = Modifier.padding(12.dp, 5.dp), onClick = { imagePickerLauncher.launch("image/*") })
                { Text("Import device images") }
                Button(modifier = Modifier.padding(12.dp, 5.dp), onClick = { navController.navigate("community")})
                { Text("Browse community images")}
                Button(modifier = Modifier.padding(12.dp, 5.dp), onClick = { onDismiss() })
                { Text("Close") }
            }
        }
    }
}