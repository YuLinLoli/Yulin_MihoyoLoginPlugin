package yulin.test

import com.alibaba.fastjson.JSON
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.commons.lang3.RandomStringUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class TestMain {
    companion object{
        val app_id = "8"
        val publicKey = "-----BEGIN PUBLIC KEY-----" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDvekdPMHN3AYhm/vktJT+YJr7cI5DcsNKqdsx5DZX0gDuWFuIjzdwButrIYPNmRJ1G8ybDIF7oDW2eEpm5sMbL9zs" +
                "9ExXCdvqrn51qELbqj0XxtMTIpaCHFSI50PfPpTFV9Xt/hmyVwokoOXFlAEgCn+Q" +
                "CgGs52bFoYMtyi+xEQIDAQAB" +
                "-----END PUBLIC KEY-----"
        @JvmStatic
        fun main(args: Array<String>) {

            httpGet(miHoYoLiginQRCode())
        }
        fun httpGet(uri: String){
            val client = OkHttpClient().newBuilder().build()
            val request = Request.Builder()
                .get()
                .url("https://api.pwmqr.com/qrcode/create/?url=${uri}")
                .build()
            val call = client.newCall(request)
            //返回请求结果
            val response = call.execute()
            val qrCodeImageUrl = response.toString().split(", url=")[1].replace("}", "")
            println(qrCodeImageUrl)
            //使用 Contact.uploadImage, 将 ExternalResource 上传得到 Image.
            //也可以使用 ExternalResource.uploadAsImage 扩展.
            val toExternalResource = httpUrlToFile(qrCodeImageUrl).toExternalResource()
//            val image = toExternalResource.uploadAsImage()
            toExternalResource.close()
        }
        fun httpUrlToFile(url: String): File{
            val httpUrl: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            httpUrl.connect()
            val ins = httpUrl.inputStream
            val file = File(System.getProperty("java.io.tmpdir") + File.separator + "xie");//System.getProperty("java.io.tmpdir")缓存
            if (file.exists()) {
                file.delete();//如果缓存中存在该文件就删除
            }
            val os = FileOutputStream(file);
            val len = 8192
            val buffer = ByteArray(len)
            val bytesRead = ins.read(buffer, 0, len)
            while (bytesRead != -1) {
                os.write(buffer, 0, bytesRead)
            }

            os.close()
            ins.close()
            return file
        }
        fun miHoYoLiginQRCode(): String{
            val device = randomStr(64)
            val array = arrayOf(app_id,device)
            val toJSONString = JSON.toJSONString(array)
            val string = "{\"app_id\":\"${app_id}\",\"device\":\"${device}\"}"
            println(string)
            //请求获取登陆地址
            val request = requestJsonPost(
                "https://hk4e-sdk.mihoyo.com/hk4e_cn/combo/panda/qrcode/fetch",
                string
            )
            val qrUrl = request!!.split("{\"url\":\"")[1].split("\"}")[0].replace("\\u0026","&")

            val ticket = qrUrl.split("icket=")[1]
            return qrUrl

        }













        fun randomStr(n: Int): String{
            return try {
                RandomStringUtils.randomAlphanumeric(n)
            }catch (e: Exception){
                e.printStackTrace()
                println("生成随机字符串错误!长度为${n}")
                "error"
            }
        }
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