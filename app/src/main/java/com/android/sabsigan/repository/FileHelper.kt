package com.android.sabsigan.repository

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
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

//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")  // 파일 형식에 따라 적절한 MIME 타입을 사용하세요.
//            // 필요한 경우 추가적인 컬럼 정보 설정 가능
//        }
//
//        // 외부 저장소에 이미지 파일을 생성
//        val externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        val resolver = context.contentResolver
//        val insertUri = resolver.insert(externalUri, contentValues)
//
//        insertUri?.let { outputStream ->
//            try {
//                resolver.openOutputStream(outputStream)?.use { os: OutputStream ->
//                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
//                        inputStream.copyTo(os)
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                return null
//            } finally {
//                resolver.notifyChange(uri, null)
//            }
//        }
//
//        return insertUri
    }

    fun saveAllFile() {

    }
}