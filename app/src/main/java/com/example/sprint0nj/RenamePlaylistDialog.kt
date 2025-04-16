package com.example.sprint0nj

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast

/**
 * @param currentName The current playlist name (to pre-populate the text field).
 * @param onDismiss Callback when the dialog is dismissed.
 * @param onConfirm Callback with the new name when the user confirms the rename.
 */
@Composable
fun RenamePlaylistDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    // text field state with the current playlist name
    var newName by remember { mutableStateOf(TextFieldValue(currentName)) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Rename Playlist") },
        text = {
            Column {
                Text(text = "Enter new playlist name:", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newName.text.isNotEmpty()) {
                        onConfirm(newName.text)  // Pass the new name back to the caller
                        onDismiss()
                    } else {
                        Toast.makeText(context, "Playlist name cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Confirm", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}
