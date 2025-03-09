package com.example.sprint0nj

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/**
 * A reusable composable that displays a plus button.
 * When clicked, it opens a dropdown menu with two options.
 *
 * @param onAddPlaylist Action to perform when "Add Playlist" is selected.
 * @param onImportPlaylist Action to perform when "Import Playlist" is selected.
 */
@Composable
fun PlusButtonWithMenu(
    onAddPlaylist: () -> Unit,
    onImportPlaylist: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // The Box is used to anchor both the Button and its DropdownMenu.
    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        // Plus button that triggers the menu
        Button(
            onClick = { menuExpanded = true },
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            contentPadding = PaddingValues(0.dp)
        ) {
            // Center the "+" text inside the button
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }

        // DropdownMenu appears when the plus button is clicked.
        // The offset can be adjusted to reposition the menu relative to the button.
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            offset = DpOffset(x = 0.dp, y = 56.dp)  // 56.dp moves it directly below the button.
        ) {
            DropdownMenuItem(
                text = { Text("Add Playlist") },
                onClick = {
                    onAddPlaylist()
                    menuExpanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Import Playlist") },
                onClick = {
                    onImportPlaylist()
                    menuExpanded = false
                }
            )
        }
    }
}
