package com.example.loginpage.ui.commonFunction

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.util.CoilUtils.result
import java.io.File
import java.io.FileOutputStream



suspend fun sharePost(context: Context, text: String, imageUrl: String? = null) {
    try {
        if (imageUrl.isNullOrEmpty()) {
            // Text-only sharing
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }

            // Let the user choose an app to share with
            val shareIntent = Intent.createChooser(sendIntent, "Share post")
            context.startActivity(shareIntent)
        } else {
            //Prepare Coil image loader to download the image from URL
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)   // Required to ensure we can convert to Bitmap
                .build()

            // Execute image request synchronously (since we're in a suspend function)
            val result = loader.execute(request)

            if (result is SuccessResult) {
                val bitmap = (result.drawable as Drawable).toBitmap()

                // Save the bitmap to a temporary file in the app's cache directory
                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs()      // Create the folder if it doesn't exist


                val file = File(cachePath, "shared_image.png")


                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                // Get a URI using FileProvider (requires proper config in Manifest and xml)
                val imageUri: Uri = FileProvider.getUriForFile(
                    context,
                    context.packageName + ".fileprovider",
                    file
                )
                // Create an intent to share both image and text
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    type = "image/*"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)     // Required for external apps
                }

                context.startActivity(Intent.createChooser(shareIntent, "Share post"))
            }
        }
    } catch (e: Exception) {
        // Log any exceptions that occur during the share process
        Log.e("ShareUtil", "Error sharing post", e)
    }
}
