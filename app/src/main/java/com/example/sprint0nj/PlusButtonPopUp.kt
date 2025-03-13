package com.example.sprint0nj

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight


// Data class representing a single menu option
// This class holds the title (what is displayed in the menu) and an action (lambda) to execute on click
data class MenuOption(
    val title: String,       // The text displayed for the menu option
    val onClick: () -> Unit  // The action executed when the option is selected
)


// Reusable composable for the plus button with a popup (dropdown) menu
// This component can be used in multiple screens by passing different lists of MenuOption items
@Composable
fun PlusButtonWithMenu(
    menuOptions: List<MenuOption>  // A list of menu options to display in the dropdown
) {
    // Local state to track whether the dropdown menu is currently expanded
    var menuExpanded by remember { mutableStateOf(false) }

    // Box is used as a container to anchor both the plus button and its dropdown menu
    // The wrapContentSize with Alignment.TopEnd places content at the top-right corner
    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {

        // Plus Button:
        // This button displays a "+" symbol and triggers the dropdown menu when clicked
        Button(
            onClick = { menuExpanded = true }, // When clicked, set menuExpanded to true to open the menu
            modifier = Modifier.size(56.dp),     // Set the fixed size of the button (can be adjusted).
            shape = RoundedCornerShape(12.dp),   // Rounded corners. Can change the dp value to alter curvature
            colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Button background color
            contentPadding = PaddingValues(0.dp)  // Remove any internal padding for a tighter layout
        ) {

            // Inner Box to center the "+" text inside the button
            Box(
                modifier = Modifier.fillMaxSize(),        // Fill the available space inside the button
                contentAlignment = Alignment.Center         // Center the text both vertically and horizontally
            ) {
                // Text displaying the plus sign
                // Can adjust fontSize and fontWeight for customization
                Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }


        // DropdownMenu:
        // This menu appears when menuExpanded is true and displays the list of menu options
        DropdownMenu(
            expanded = menuExpanded,                   // Controls whether the menu is visible
            onDismissRequest = { menuExpanded = false }, // Callback to close the menu when clicked outside
            // The offset positions the dropdown menu relative to the plus button
            // Change the DpOffset values to adjust horizontal (x) or vertical (y) position
            offset = DpOffset(0.dp, 0.dp)
        ) {
            // Loop through each menu option provided in the list
            menuOptions.forEach { option ->
                // Each option is displayed as a DropdownMenuItem
                DropdownMenuItem(
                    text = {
                        // Display the title of the menu option
                        Text(option.title)
                    },
                    onClick = {
                        // Execute the action associated with the menu option
                        option.onClick()
                        // Close the dropdown menu after an option is selected
                        menuExpanded = false
                    }
                )
            }
        }
    }
}
