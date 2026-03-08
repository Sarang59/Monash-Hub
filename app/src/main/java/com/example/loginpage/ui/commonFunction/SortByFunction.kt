package com.example.loginpage.ui.commonFunction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginpage.ui.theme.primaryBg
import com.example.loginpage.ui.theme.primaryText
import com.google.android.play.integrity.internal.o
import android.util.Log
import androidx.compose.material3.MenuDefaults

//Composable function that displays a Sort button (icon) with a dropdown menu.
// When clicked, the user can select a sorting option from the menu.
@Composable
fun SortByFunction (
    sortOptions: List<Pair<String, () -> Unit>>,
    sortSelected: MutableState<String>
) {
    var sortExpanded by remember { mutableStateOf(false) } // List of sort labels and their associated actions
    val colorScheme = MaterialTheme.colorScheme     // Currently selected sorting label
    Box()
    {
        IconButton(
            onClick = { sortExpanded = !sortExpanded },
            modifier = Modifier.size(28.dp)
        ) {
            Log.d("sort", "${colorScheme.primaryText}")
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "Sort By",
                tint = colorScheme.primaryText,
                modifier = Modifier.size(28.dp)
            )
        }
        // Dropdown menu that appears below the sort icon
        DropdownMenu(
            expanded = sortExpanded,
            onDismissRequest = { sortExpanded = false },
            modifier = Modifier.padding(0.dp)
            .background(colorScheme.primaryBg)
        ) {
            // Render each item in the sortOptions list
            sortOptions.forEach { (label, onClickAction) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            fontWeight = if (sortSelected.value == label) FontWeight.W900 else FontWeight.Normal,
                            modifier = Modifier.padding(5.dp),
                            color = colorScheme.primaryText
                        ) },
                    onClick = {
                        sortSelected.value = label       // Update selected sort state
                        onClickAction()                 // Invoke associated action
                        sortExpanded = false                // Close the dropdown
                    },

                    colors = MenuDefaults.itemColors(
                        textColor = colorScheme.primaryText     // Use theme text color
                    )
                )
            }
        }
    }
}