package com.yulin.kotlinUtil

import com.yulin.kotlinUtil.RandomUtil.Companion.randomStr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.internal.deps.okhttp3.MediaType.Companion.toMediaTypeOrNull
import net.mamoe.mirai.internal.deps.okhttp3.OkHttpClient
import net.mamoe.mirai.internal.deps.okhttp3.Request
import net.mamoe.mirai.internal.deps.okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class HttpRequestUtil {
    companion object {
        /**
         * 获取并返回Json字符串（POST方法）为空的时候返回null
         * @param uri URL链接地址
         * @return responseData jsonString或者为空
         * @author 岚雨凛 <cheng_ying@outlook.com>
         */
        suspend fun requestJsonPost(uri: String, jsonString: String): String? {
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
        suspend fun httpUrlToFile(url: String): File {
            val httpUrl: HttpURLConnection = withContext(Dispatchers.IO) {
                URL(url).openConnection()
            } as HttpURLConnection
            withContext(Dispatchers.IO) {
                httpUrl.connect()
            }
            val ins = httpUrl.inputStream
            val file = File(System.getProperty("java.io.tmpdir") + File.separator + "xie");//System.getProperty("java.io.tmpdir")缓存
            if (file.exists()) {
                file.delete();//如果缓存中存在该文件就删除
            }
            val os = withContext(Dispatchers.IO) {
                FileOutputStream(file)
            };
            val len = 8192
            val buffer = ByteArray(len)
            val bytesRead = withContext(Dispatchers.IO) {
                ins.read(buffer, 0, len)
            }
            while (bytesRead != -1) {
                withContext(Dispatchers.IO) {
                    os.write(buffer, 0, bytesRead)
                    os.close()
                    ins.close()
                }
            }
            return file
        }
        suspend fun httpGet(url: String): String {
            val client = OkHttpClient().newBuilder().build()
            val request = Request.Builder()
                .get()
                .url(url)
                .build()
            val call = client.newCall(request)
            //返回请求结果
            val response = call.execute()
            return response.body?.string().toString()
        }
        suspend fun mihoyoHttpRequest(url: String, jsonString: String, aigis: String): String?{
            val client = OkHttpClient().newBuilder().build()
            val body = jsonString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val request = Request.Builder()
                .post(body)
                .url(url)
                .header("x-rpc-app_version","2.41.0")
                .header("DS",jsonString)
                .header("x-rpc-aigis",aigis)
                .header("Content-Type","application/json")
                .header("Accept","application/json")
                .header("x-rpc-game_biz","bbs_cn")
                .header("x-rpc-sys_version","12")
                .header("x-rpc-device_id", randomStr(16))
                .header("x-rpc-device_fp", randomStr(13))
                .header("x-rpc-device_name", randomStr(16))
                .header("x-rpc-device_model", randomStr(16))
                .header("x-rpc-app_id","bll8iq97cem8")
                .header("x-rpc-client_type","2")
                .header("User-Agent","okhttp/4.8.0")
                .build()
            val call = client.newCall(request)
            //返回请求结果
            val response = call.execute()
            return response.body?.string()

        }
    }
}