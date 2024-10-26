package com.betterlife.antifragile.config


import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder
import com.google.mediapipe.tasks.text.textembedder.TextEmbedder.TextEmbedderOptions

class TextEmbedder(
    private val context: Context,
    var currentDelegate: Int = DELEGATE_CPU,
    var currentModel: Int = MODEL_MOBILE_BERT,
    var listener: EmbedderListener? = null
) {
    private var textEmbedder: TextEmbedder? = null

    init {
        setupTextEmbedder()
    }

    private fun setupTextEmbedder() {
        val baseOptionsBuilder = BaseOptions.builder()
        when (currentDelegate) {
            DELEGATE_CPU -> {
                baseOptionsBuilder.setDelegate(Delegate.CPU)
            }
            DELEGATE_GPU -> {
                baseOptionsBuilder.setDelegate(Delegate.GPU)
            }
        }
        when (currentModel) {
            MODEL_MOBILE_BERT -> {
                baseOptionsBuilder.setModelAssetPath(MODEL_MOBILE_BERT_PATH)
            }
            MODEL_AVERAGE_WORD -> {
                baseOptionsBuilder.setModelAssetPath(MODEL_AVERAGE_WORD_PATH)
            }
        }
        try {
            val baseOptions = baseOptionsBuilder.build()
            val optionsBuilder =
                TextEmbedderOptions.builder().setBaseOptions(baseOptions)
            val options = optionsBuilder.build()
            textEmbedder = TextEmbedder.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            listener?.onError(
                "Text embedder failed to initialize. See error logs for " +
                        "details"
            )
            Log.e(
                TAG,
                "Text embedder failed to load model with error: " + e.message
            )
        } catch (e: RuntimeException) {
            // This occurs if the model being used does not support GPU
            listener?.onError(
                "Text embedder failed to initialize. See error logs for " +
                        "details", GPU_ERROR
            )
            Log.e(
                TAG,
                "Text embedder failed to load model with error: " + e.message
            )
        }
    }

    fun extractEmbeddings(text: String): String? {
        return textEmbedder?.let {
            val embedResult = it.embed(text).embeddingResult().embeddings()

            if (embedResult.isNotEmpty()) {
                embedResult.first().floatEmbedding().joinToString(", ")
            } else {
                null
            }
        }
    }

    fun clearTextEmbedder() {
        textEmbedder?.close()
        textEmbedder = null
    }

    // Wraps results from inference, the time it takes for inference to be
    // performed.
    data class ResultBundle(
        val similarity: Double,
        val inferenceTime: Long,
    )

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val MODEL_MOBILE_BERT = 0
        const val MODEL_AVERAGE_WORD = 1
        const val MODEL_MOBILE_BERT_PATH = "mobile_bert.tflite"
        const val MODEL_AVERAGE_WORD_PATH = "average_word.tflite"
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
        private const val TAG = "TextEmbedder"
    }

    interface EmbedderListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
    }
}
