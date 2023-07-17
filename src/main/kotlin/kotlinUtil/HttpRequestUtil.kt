package yulin.kotlinUtil

import net.mamoe.mirai.internal.deps.okhttp3.MediaType
import net.mamoe.mirai.internal.deps.okhttp3.MediaType.Companion.toMediaTypeOrNull
import net.mamoe.mirai.internal.deps.okhttp3.OkHttpClient
import net.mamoe.mirai.internal.deps.okhttp3.Request
import net.mamoe.mirai.internal.deps.okhttp3.RequestBody
import net.mamoe.mirai.internal.deps.okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class HttpRequestUtil {
    companion object {
        /**
         * 获取并返回Json字符串（POST方法）为空的时候返回null
         * @param uri URL链接地址
         * @return responseData jsonString或者为空
         * @author 岚雨凛 <cheng_ying@outlook.com>
         */
        fun requestJsonPost(uri: String, jsonString: String): String? {
            val client = OkHttpClient().newBuilder().build()
            val body = jsonString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val request = Request.Builder()
                .post(body)
                .url(uri)
                .build()
            val call = client.newCall(request)
            //返回请求结果
            return try {
                val response = call.execute()
                response.body?.string()
            } catch (e: IOException) {
                println("请求网址${uri}错误，JsonData为“${jsonString}”")
                e.printStackTrace()
                null
            }
        }


    }
}