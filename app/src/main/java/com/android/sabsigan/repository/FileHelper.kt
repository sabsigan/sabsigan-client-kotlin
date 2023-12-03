package com.android.sabsigan.repository

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class FileHelper(private val context: Context) {
    fun saveBitmapFile(bitmap: Bitmap, fileName: String) {
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val path = "$folder/$fileName"
        val extension = fileName.split(".")

        val fos: FileOutputStream
        try{
            fos = FileOutputStream(File(path))
            if (extension[1] == "png")
                bitmap.compress(Bitmap.CompressFormat.PNG,100,fos)
            else
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)

            fos.close()
            Toast.makeText(context, "파일을 저장하였습니다.", Toast.LENGTH_SHORT).show()
        }catch (e: IOException) {
            Toast.makeText(context, "파일 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun saveAllFile(uri: Uri, fileName: String): File? {
//        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
//        val path = "$folder/$fileName"
//        val extension = fileName.split(".")
//
//        val fos: FileOutputStream
//        try{
//            fos = FileOutputStream(File(path))
//
//            fos.close()
//            Toast.makeText(context, "파일을 저장하였습니다.", Toast.LENGTH_SHORT).show()
//        }catch (e: IOException) {
//            Toast.makeText(context, "파일 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//        }
//
//
//
//        val contentResolver: ContentResolver = context.contentResolver
//
//        if (fileName != null) {
//            val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
//            val outputFile = File(outputDir, fileName)
//
//            contentResolver.openInputStream(uri)?.use { inputStream ->
//                FileOutputStream(outputFile).use { outputStream ->
//                    val buffer = ByteArray(4 * 1024) // Adjust buffer size as needed
//                    var read: Int
//                    while (inputStream.read(buffer).also { read = it } != -1) {
//                        outputStream.write(buffer, 0, read)
//                    }
//                    outputStream.flush()
//                }
//            }
//
//            return outputFile
//        }

        return null
    }
}