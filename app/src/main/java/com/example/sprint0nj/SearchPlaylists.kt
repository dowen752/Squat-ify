package com.example.sprint0nj

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.wrapContentSize

@Composable
fun SearchPlaylistsButton(
    onSearch: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.size(56.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search playlists",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    if (showDialog) {
        SearchPlaylistsDialog(
            onDismiss = { showDialog = false },
            onConfirm = { query ->
                onSearch(query)
                showDialog = false
            }
        )
    }
}


// Window that lets the user type in a playlist name to search for

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPlaylistsDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search Playlists") },
        text = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Playlist name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onConfirm(searchQuery) }),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(searchQuery) }) {
                Text("Search")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
