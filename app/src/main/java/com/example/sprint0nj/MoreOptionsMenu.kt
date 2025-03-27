package com.example.sprint0nj

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp

/*
 * This is a separate composable for the "..." button, so you can import it wherever needed.
 * "Toast" is an Android API used to display short confirmation messages after clicking the buttons.
 */
@Composable
fun MoreOptionsMenu(
    onShare: () -> Unit,
    onRemove: () -> Unit
) {
    // State to track whether the dropdown menu is expanded
    var expanded by remember { mutableStateOf(false) }

    // "IconButton" is used for the "..." text
    IconButton(onClick = { expanded = true }) {
        // "..." icon is represented as text here; you can replace it with an Icon if desired
        Text(text = "...", fontSize = 20.sp)
    }
    // The DropdownMenu is displayed when "expanded" is true
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        // Option to Share
        DropdownMenuItem(
            text = { Text("Share") },
            onClick = {
                expanded = false
                onShare()
            }
        )
        // Option to Remove (placeholder only; no real removal logic)
        DropdownMenuItem(
            text = { Text("Remove") },
            onClick = {
                expanded = false
                onRemove()
            }
        )
    }
}
