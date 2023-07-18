package com.yulin.main

import com.alibaba.fastjson2.JSONObject
import com.yulin.kotlinUtil.HttpRequestUtil.Companion.httpGet
import com.yulin.kotlinUtil.HttpRequestUtil.Companion.mihoyoHttpRequest
import com.yulin.kotlinUtil.HttpRequestUtil.Companion.requestJsonPost
import com.yulin.kotlinUtil.MihoyoUtil
import com.yulin.kotlinUtil.MihoyoUtil.Companion.miHoYoLiginQRCode
import com.yulin.kotlinUtil.QRCodeInit.Companion.qrCodeGenerate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage

class MihoyoLoginQRcode {
    companion object {
        suspend fun qrCodeMain(event: MessageEvent) {
            if (!event.message.contentToString().contains("米哈游") && !event.message.contentToString()
                    .contains("登录")
            ) {
                return
            }
            //获取QRcode
            val qrCode = miHoYoLiginQRCode()
            val qrLink = qrCode[0]
            val ticket = qrCode[1]
            val toExternalResource = qrCodeGenerate(qrLink, 300, 300, 90, 120, "null", 0x6495ED)
            val image = toExternalResource!!.uploadAsImage(event.subject)
            event.subject.sendMessage(image)
            event.subject.sendMessage("请扫描二维码登录！")
            withContext(Dispatchers.IO) {
                toExternalResource.close()
            }
            var data: Array<String> = arrayOf()
            for (i in 1 until 60) {
                withContext(Dispatchers.IO) {
                    Thread.sleep(5000)
                }
                val post = requestJsonPost(
                    "https://hk4e-sdk.mihoyo.com/hk4e_cn/combo/panda/qrcode/query",
                    "{\"app_id\":\"${MihoyoUtil.appId}\",\"device\":\"${qrCode[2]}\",\"ticket\":\"${ticket}\"}"
                )
                if (post == null) {
                    event.subject.sendMessage("错误！post为空！")
                    return
                }
                if (post.contains("ExpiredCode")) {
                    event.subject.sendMessage("二维码已过期，请重新登录!")
                    return
                }
                val jsonObject = JSONObject.parseObject(post)
                val code = jsonObject["retcode"]
                val stat = JSONObject.parseObject(jsonObject["data"].toString())["stat"]
                if (code != 0) {
                    event.subject.sendMessage("二维码已过期，请重新登录!")
                    return
                }
                if (stat == "Scanned") {
                    event.subject.sendMessage("二维码已扫描，请确认登录!")
                }
                if (stat == "Confirmed") {
                    val raw = JSONObject.parseObject(
                        JSONObject.parseObject(
                            JSONObject.parseObject(jsonObject["data"].toString())["payload"]
                                .toString()
                        ).toString()
                    )["raw"]

                    val uid = JSONObject.parseObject(raw.toString())["uid"].toString()
                    val token = JSONObject.parseObject(raw.toString())["token"].toString()
                    data = arrayOf(
                        uid,
                        token
                    )
                    break
                }

            }
            val jsonPost = mihoyoHttpRequest(
                "https://passport-api.mihoyo.com/account/ma-cn-session/app/getTokenByGameToken",
                "{\"account_id\":${data[0]},\"game_token\":\"${data[1]}\"}",
                ""
            )
            val httpGet =
                httpGet("https://api-takumi.mihoyo.com/auth/api/getCookieAccountInfoByGameToken?account_id=${data[0]}&game_token=${data[1]}")
            if (jsonPost == null) {
                return
            }

            val jsonPostJson = JSONObject.parseObject(jsonPost)["data"].toString()
            val tokenJson = JSONObject.parseObject(jsonPostJson)["token"].toString()
            val userInfo = JSONObject.parseObject(jsonPostJson)["user_info"].toString()
            val httpGetJsonData = JSONObject.parseObject(httpGet)["data"].toString()

            val token = JSONObject.parseObject(tokenJson)["token"].toString()
            val aid = JSONObject.parseObject(userInfo)["aid"].toString()
            val mid = JSONObject.parseObject(userInfo)["mid"].toString()
            val cookieToken = JSONObject.parseObject(httpGetJsonData)["cookie_token"].toString()

            event.subject.sendMessage("ltoken=${token};ltuid=${aid};cookie_token=${cookieToken}")
            event.subject.sendMessage("stoken=${token};stuid=${aid};mid=${mid}")
            event.subject.sendMessage("登录完成，以上分别是 Cookie 和 Stoken，分别把两条消息逐条转发给 Bot 完成绑定!(Bot因为特殊原因不会发送绑定成功哦，可以直接在群里试试 #深渊 指令)")
        }
    }
}