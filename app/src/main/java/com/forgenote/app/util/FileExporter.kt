package com.forgenote.app.util

import android.content.Context
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

object FileExporter {
    fun exportToDownloads(context: Context, title: String, body: String, isHtml: Boolean) {
        val extension = if (isHtml) ".html" else ".md"
        val cleanTitle = title.replace(Regex("[^a-zA-Z0-9_-]"), "_").ifBlank { "Untitled_Note" }
        val filename = "${cleanTitle}_${System.currentTimeMillis()}$extension"

        val content = if (isHtml) {
            val safeTitle = escapeHtml(title)
            val safeBody = escapeHtml(body)
            """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <title>$safeTitle</title>
                <style>
                    body { font-family: sans-serif; line-height: 1.6; padding: 20px; color: #2C2C2C; background: #F9F9F9; }
                    h1 { color: #2A7A8C; border-bottom: 2px solid #2A7A8C; padding-bottom: 8px; }
                    .content { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); white-space: pre-wrap; }
                </style>
            </head>
            <body>
                <h1>$safeTitle</h1>
                <div class="content">$safeBody</div>
            </body>
            </html>
            """.trimIndent()
        } else {
            "# $title\n\n$body"
        }

        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            val targetFile = File(downloadsDir, filename)
            FileOutputStream(targetFile).use { output ->
                output.write(content.toByteArray())
            }
            Toast.makeText(context, "Exported successfully to Downloads!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Export Failure: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
    }
}
