package com.betterlife.antifragile.presentation.util

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ModelDownloader(private val context: Context) {

    private val modelFileName = "gemma2b-cpu.bin"
    private val modelFilePath = File(context.filesDir, "llm/$modelFileName")

    fun isModelAlreadyDownloaded(): Boolean {
        return modelFilePath.exists()
    }

    fun downloadModel(
        url: String,
        onProgressUpdate: (Int) -> Unit,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        Thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    response.body?.let { body ->
                        val totalSize = body.contentLength()  // 파일 크기를 정확하게 가져오기 위해 contentLength() 사용
                        val inputStream = body.byteStream()
                        saveFile(inputStream, totalSize, onProgressUpdate)
                        onSuccess()
                    } ?: throw Exception("Empty response body")
                } else {
                    onFailure()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onFailure()
            }
        }.start()
    }

    private fun saveFile(inputStream: InputStream, totalSize: Long, onProgressUpdate: (Int) -> Unit) {
        if (!modelFilePath.parentFile.exists()) {
            modelFilePath.parentFile.mkdirs()  // 경로가 존재하지 않으면 생성
        }

        val outputFile = FileOutputStream(modelFilePath)
        val buffer = ByteArray(4 * 1024)
        var bytesRead: Int
        var totalBytesRead: Long = 0

        // 파일 저장 중 진행률 업데이트
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputFile.write(buffer, 0, bytesRead)
            totalBytesRead += bytesRead
            val progress = (totalBytesRead * 100 / totalSize).toInt()  // 진행률 계산
            onProgressUpdate(progress)
        }

        outputFile.close()
        inputStream.close()
    }
}