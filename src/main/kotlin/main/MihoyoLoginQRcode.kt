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
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage

class MihoyoLoginQRcode {
    companion object {
        suspend fun qrCodeMain(event: MessageEvent): Map<String, String>? {
            //获取QRcode相关数据
            val qrCode = miHoYoLiginQRCode()
            val qrLink = qrCode[0]
            val ticket = qrCode[1]
            //根据二维码链接创建二维码
            val toExternalResource = qrCodeGenerate(qrLink, 300, 300, 90, 120, "null", 0x6495ED)
            //上传二维码图片
            val image = toExternalResource.use {
                it.uploadAsImage(event.subject)
            }
            //发送并提醒用户扫描二维码

            val message = MessageChainBuilder()
                .append(image)
                .append("请扫描二维码登录！")
                .build()
            event.subject.sendMessage(message)


            //创建一个空的data数组用于之后放uid与token
            var data: Array<String> = arrayOf()
            //循环60次来进行每5秒一次的查询二维码状态
            for (i in 1 until 60) {
                //休眠5秒
                withContext(Dispatchers.IO) {
                    Thread.sleep(5000)
                }
                //请求二维码状态
                val post = requestJsonPost(
                    "https://hk4e-sdk.mihoyo.com/hk4e_cn/combo/panda/qrcode/query",
                    "{\"app_id\":\"${MihoyoUtil.appId}\",\"device\":\"${qrCode[2]}\",\"ticket\":\"${ticket}\"}"
                )
                //如果返回状态为空则报错停止
                if (post == null) {
                    event.subject.sendMessage("错误！post为空！")
                    return null
                }
                //如果包含ExpiredCode字符则二维码已过期停止
                if (post.contains("ExpiredCode")) {
                    event.subject.sendMessage("二维码已过期，请重新登录!")
                    return null
                }
                //获取json对象
                val jsonObject = JSONObject.parseObject(post)
                //获取retcode值
                val code = jsonObject["retcode"]
                //获取stat二维码状态
                val stat = JSONObject.parseObject(jsonObject["data"].toString())["stat"]
                //如果包code不等于0，则二维码已过期！
                if (code != 0) {
                    event.subject.sendMessage("二维码已过期，请重新登录!")
                    return null
                }
                //如果stat二维码状态为Scanned，则二维码已扫描等待用户确认登录
                if (stat == "Scanned") {
                    event.subject.sendMessage("二维码已扫描，请确认登录!")
                }
                //如果stat二维码状态为Confirmed，则获取uid以及token数据用于下面的请求账户的有效密钥
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
            //请求接口获得token、aid、mid
            val jsonPost = mihoyoHttpRequest(
                "https://passport-api.mihoyo.com/account/ma-cn-session/app/getTokenByGameToken",
                "{\"account_id\":${data[0]},\"game_token\":\"${data[1]}\"}",
                ""
            )
            //请求接口获得cookie_token
            val httpGet =
                httpGet("https://api-takumi.mihoyo.com/auth/api/getCookieAccountInfoByGameToken?account_id=${data[0]}&game_token=${data[1]}")
            if (jsonPost == null) {
                event.subject.sendMessage("错误！最后一步jsonPost报错！请联系插件作者或者机器人主人来处理此问题")
                return null
            }

            val jsonPostJson = JSONObject.parseObject(jsonPost)["data"].toString()
            val tokenJson = JSONObject.parseObject(jsonPostJson)["token"].toString()
            val userInfo = JSONObject.parseObject(jsonPostJson)["user_info"].toString()
            val httpGetJsonData = JSONObject.parseObject(httpGet)["data"].toString()

            val token = JSONObject.parseObject(tokenJson)["token"].toString()
            val aid = JSONObject.parseObject(userInfo)["aid"].toString()
            val mid = JSONObject.parseObject(userInfo)["mid"].toString()
            val cookieToken = JSONObject.parseObject(httpGetJsonData)["cookie_token"].toString()

            return mapOf("token" to token, "aid" to aid, "mid" to mid, "cookieToken" to cookieToken)
        }
    }
}