package com.yulin.main

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import com.yulin.kotlinUtil.HttpRequestUtil.Companion.httpGet
import com.yulin.kotlinUtil.HttpRequestUtil.Companion.httpUrlToFile
import com.yulin.kotlinUtil.HttpRequestUtil.Companion.mihoyoHttpRequest
import com.yulin.kotlinUtil.HttpRequestUtil.Companion.requestJsonPost
import com.yulin.kotlinUtil.MihoyoUtil
import com.yulin.kotlinUtil.MihoyoUtil.Companion.miHoYoLiginQRCode
import com.yulin.kotlinUtil.QRCodeInit.Companion.qrCodeGenerate

class MihoyoLoginQRcode {
    companion object{
        suspend fun qrCodeMain(event: MessageEvent){
            if(!event.message.contentToString().contains("米哈游") && !event.message.contentToString().contains("登录")){
                return
            }
            //获取QRcode
            val qrCode = miHoYoLiginQRCode()
            val qrLink = qrCode[0]
            val ticket = qrCode[1]
            val toExternalResource = qrCodeGenerate(qrLink,300,300,90,120,"null",0x6495ED)
            val image = toExternalResource.uploadAsImage(event.subject)
            event.subject.sendMessage(image)
            event.subject.sendMessage("请扫描二维码登录！")
            withContext(Dispatchers.IO) {
                toExternalResource.close()
            }
            var data: Array<String> = arrayOf()
            for (i in 1 until 60){
                withContext(Dispatchers.IO) {
                    Thread.sleep(5000)
                }
                val post = requestJsonPost(
                    "https://hk4e-sdk.mihoyo.com/hk4e_cn/combo/panda/qrcode/query",
                    "{\"app_id\":\"${MihoyoUtil.appId}\",\"device\":\"${qrCode[2]}\",\"ticket\":\"${ticket}\"}"
                )
                println(post)
                if (post == null){
                    event.subject.sendMessage("错误！post为空！")
                    return
                }
                val code = post.split("\"retcode\":")[1].split(",")[0].toInt()
                val stat = post.split("\"stat\":\"")[1].split("\",")[0]

                if (code != 0){
                    event.subject.sendMessage("二维码已过期，请重新登录!")
                    return
                }
                if (stat == "Scanned"){
                    event.subject.sendMessage("二维码已扫描，请确认登录!")
                }
                if (stat == "Confirmed"){
                    data = arrayOf(post.split("uid\\\":\\\"")[1].split("\\\"")[0],post.split("token\\\":\\\"")[1].split("\\\"")[0])
                    break
                }

            }
            val jsonPost = mihoyoHttpRequest(
                "https://passport-api.mihoyo.com/account/ma-cn-session/app/getTokenByGameToken",
                "{\"account_id\":${data[0]},\"game_token\":\"${data[1]}\"}",
                ""
            )
            println(jsonPost)
            val httpGet =
                httpGet("https://api-takumi.mihoyo.com/auth/api/getCookieAccountInfoByGameToken?account_id=${data[0]}&game_token=${data[1]}")
            println(httpGet)
            if (jsonPost == null){
                return
            }
            val token = jsonPost.split("token_type")[1].split("token\":\"")[1].split("\"}")[0]
            val aid = jsonPost.split("aid\":\"")[1].split("\",")[0]
            val mid = jsonPost.split("mid\":\"")[1].split("\",")
            val cookieToken = httpGet.split("cookie_token\":\"")[1].split("\"}")[0]
            event.subject.sendMessage("ltoken=${token};ltuid=${aid};cookie_token=${cookieToken}")
            event.subject.sendMessage("stoken=${token};stuid=${aid};mid=${mid}")
            event.subject.sendMessage("登录完成，以上分别是 Cookie 和 Stoken，发送给 Bot 完成绑定!")
        }
    }
}