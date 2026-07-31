package com.threesa.billing.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import java.io.File

object PdfDownloader {
    fun saveBase64PdfToDownloads(context: Context, fileName: String, base64Data: String): Boolean {
        return try {
            val cleanBase64 = base64Data
                .substringAfter("base64,")
                .trim()
                .replace("\n", "")
                .replace("\r", "")
            val pdfBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            val cleanFileName = if (fileName.endsWith(".pdf", ignoreCase = true)) fileName else "$fileName.pdf"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, cleanFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(pdfBytes)
                    }
                    true
                } else false
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = File(downloadsDir, cleanFileName)
                file.writeBytes(pdfBytes)
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
