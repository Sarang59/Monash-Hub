package com.example.loginpage.ui.commonFunction

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

//Loads a drawable resource dynamically by its name and returns a Painter object for use in Jetpack Compose UI.
@Composable
fun loadDrawableByName(context: Context, drawableName: String?): Painter {
    // Dynamically retrieve the drawable resource ID using the resource name
    val resId = remember(drawableName) {
        context.resources.getIdentifier(drawableName, "drawable", context.packageName)
    }
    // Return a Painter object for the resolved drawable resource
    return painterResource(id = resId)
}