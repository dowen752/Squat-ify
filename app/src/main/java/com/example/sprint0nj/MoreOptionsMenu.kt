package com.example.sprint0nj

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp



/*
 * This is a separate composable for the "..." button, so you can import it wherever needed.
 */
@Composable
fun MoreOptionsMenu(
    onShare: (() -> Unit)? = null,
    onRemove: () -> Unit,
    onEdit: () -> Unit, // New parameter for "Edit" option
    onTutorial: (() -> Unit)? = null  // New optional parameter
) {
    // State to track whether the dropdown menu is expanded
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = true }) {
            Text(text = "...", fontSize = 20.sp , color = Color.White)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset((-10).dp, (-6).dp)
        ) {
            // Only show the Share option if a callback is provided.
            if (onShare != null) {
                DropdownMenuItem(
                    text = { Text("Share") },
                    onClick = {
                        expanded = false
                        onShare()
                    }
                )
            }
            // Option to Edit
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    expanded = false
                    onEdit()
                }
            )

            // Conditionally add "Tutorial" option if a callback is provided
            if (onTutorial != null) {
                DropdownMenuItem(
                    text = { Text("Tutorial") },
                    onClick = {
                        expanded = false
                        onTutorial()
                    }
                )
            }

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
}