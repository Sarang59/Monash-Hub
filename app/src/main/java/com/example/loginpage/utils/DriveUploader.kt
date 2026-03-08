package com.example.loginpage.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.Permission
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

/**
 * Utility class for uploading files to Google Drive using a service account.
 * This implementation uses Google's REST API client libraries to authenticate and upload.
 **/
class DriveUploader(private val context: Context) {

    // Lazy initialization of the Google Drive API client
    private val driveService: Drive by lazy {
        val transport = GoogleNetHttpTransport.newTrustedTransport()    // Secure HTTP transport
        val jsonFactory = GsonFactory.getDefaultInstance()

        // Load credentials from service account JSON in assets
        val credentialsStream = context.assets.open("service_account.json")
        val credential = GoogleCredential.fromStream(credentialsStream)
            .createScoped(listOf("https://www.googleapis.com/auth/drive.file")) // Scoped to file upload access

        // Build and return the Drive service client
        Drive.Builder(transport, jsonFactory, credential)
            .setApplicationName("DriveUploader")
            .build()
    }

    /**
     * Uploads an image file to Google Drive from a URI (e.g., image picked by the user).
     * */
    fun uploadFileFromUri(uri: Uri, callback: (String?) -> Unit) {
        Thread {
            // Convert URI to a temporary file in the app cache directory
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val file = java.io.File(context.cacheDir, "${UUID.randomUUID()}.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                outputStream.close()

                // Metadata for the file to upload
                val fileMetadata = File().apply {
                    name = file.name
                    parents = listOf("1a1XEykojfXIwRGiILJWeWGq8LNuNHrfv")
                }

                // File content type and actual file
                val mediaContent = FileContent("image/jpeg", file)
                // Upload the file to Google Drive
                val uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, webContentLink, webViewLink")
                    .execute()


                // Make file public
                val permission = Permission().apply {
                    type = "anyone"
                    role = "reader"
                }
                driveService.permissions().create(uploadedFile.id, permission).execute()

                val fileUrl = uploadedFile.webContentLink

                Log.d("DriveUploader", "File uploaded: $fileUrl")

                callback(uploadedFile.webContentLink)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DriveUploader", "Upload error: ${e.message}", e)
                callback(null)
            }
        }.start()  // Run in background thread to avoid blocking UI
    }
}
