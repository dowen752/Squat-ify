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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp



/*
 * This is a separate composable for the "..." button, so you can import it wherever needed.
 * "Toast" is an Android API used to display short confirmation messages after clicking the buttons.
 */
@Composable
fun MoreOptionsMenu(
    onShare: () -> Unit,
    onRemove: () -> Unit,
    onEdit: () -> Unit // New parameter for "Edit" option
) {
    // State to track whether the dropdown menu is expanded
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = true }) {
            Text(text = "...", fontSize = 20.sp)
        }
        // The DropdownMenu is displayed when "expanded" is true
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            // Can adjust the offset to control the dropdown's position relative to the button
            offset = DpOffset((-10).dp, (-6).dp)
        ) {
            // Option to Share
            DropdownMenuItem(
                text = { Text("Share") },
                onClick = {
                    expanded = false
                    onShare()
                }
            )
            // Option to Edit
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    expanded = false
                    onEdit()
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
}