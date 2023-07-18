package com.yulin.kotlinUtil


import com.alibaba.fastjson2.JSONObject
import com.yulin.kotlinUtil.HttpRequestUtil.Companion.requestJsonPost
import com.yulin.kotlinUtil.RandomUtil.Companion.randomStr

class MihoyoUtil {
    companion object {
        const val appId = "8"

        //请求使用二维码登陆，并返回登陆的二维码以及ticket
        fun miHoYoLiginQRCode(): Array<String> {
            val device = randomStr(64)
            //请求获取登陆ticket
            val request = requestJsonPost(
                "https://hk4e-sdk.mihoyo.com/hk4e_cn/combo/panda/qrcode/fetch",
                "{\"app_id\":\"${appId}\",\"device\":\"${device}\"}"
            )
            val data = JSONObject.parseObject(request)["data"].toString()
            val qrUrl = JSONObject.parseObject(data)["url"].toString().replace("\\u0026", "&")
            val ticket = qrUrl.split("icket=")[1]
            return arrayOf(qrUrl, ticket, device)
        }

    }
}