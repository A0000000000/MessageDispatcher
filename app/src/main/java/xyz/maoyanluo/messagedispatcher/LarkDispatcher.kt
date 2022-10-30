package xyz.maoyanluo.messagedispatcher

import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.nd4j.shade.jackson.databind.ObjectMapper

class LarkDispatcher {

    private val httpRequestThread = HandlerThread("http-request-thread")
    private val apis = HashSet<String>()

    private val objectMapper = ObjectMapper()
    private val jsonType = "application/json".toMediaType()
    private val okHttpClient = OkHttpClient()

    private var handler: Handler

    init {
        httpRequestThread.start()
        handler = Handler(httpRequestThread.looper)
    }

    fun addApi(api: String) {
        apis.add(api)
    }

    fun removeApi(api: String) {
        apis.remove(api)
    }

    fun postMessage(message: String) {
        val body = createRequestFormat(message)
        val requestBody = body.toRequestBody(jsonType)
        for (api in apis) {
            val request = Request
                .Builder()
                .url(api)
                .post(requestBody)
                .build()
            handler.post {
                val response = okHttpClient.newCall(request).execute()
                Log.d("LarkDispatcher", response.body.toString())
            }
        }
    }

    private fun createRequestFormat(message: String): String {
        val params = HashMap<String, String>()
        params["msg_type"] = "text"
        val content = HashMap<String, String>()
        content["text"] = message
        params["content"] = objectMapper.writeValueAsString(content)
        return objectMapper.writeValueAsString(params) ?: ""
    }

}
