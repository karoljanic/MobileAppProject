package org.mobileapp.local_data

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toFile
import androidx.core.net.toUri
import org.mobileapp.settings.Settings
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

object LocalData {
    fun getTextFileStream(context: Context, uri: Uri): InputStream? {
        var stream: InputStream? = null
        try {
            stream = context.contentResolver.openInputStream(uri)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return stream
    }


    fun getFileSize(context: Context, uri: Uri): Long {
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)

        return if (cursor != null) {
            val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            val size: Long = cursor.getLong(sizeIndex)
            cursor.close()
            size
        } else 0
    }


    fun getFileName(context: Context, uri: Uri): String {
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)

        return if (cursor != null) {
            val nameIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            val name: String = cursor.getString(nameIndex)
            cursor.close()
            name
        } else ""
    }

    fun getTempFileUri(context: Context): Uri {
        return File(context.getExternalFilesDir(Settings.TEMPORARY_TRACK_FOLDER), Settings.TEMPORARY_TRACK_FILE).toUri()
    }

    fun deleteTempFile(context: Context) {
        getTempFileUri(context).toFile().delete()
    }

    fun readTextFile(context: Context, fileUri: Uri): String {
        val file: File = fileUri.toFile()

        if (!file.exists()) {
            return ""
        }

        val stream: InputStream = file.inputStream()
        val reader: BufferedReader = BufferedReader(InputStreamReader(stream))
        val builder: StringBuilder = StringBuilder()

        reader.forEachLine {
            builder.append(it)
            builder.append("\n")
        }
        stream.close()

        return builder.toString()
    }

    fun writeTextFile(text: String, fileUri: Uri) {
        if (text.isNotEmpty()) {
            val file: File = fileUri.toFile()
            file.writeText(text)
        }
    }
}