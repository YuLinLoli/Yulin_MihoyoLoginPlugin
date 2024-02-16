package com.yulin.kotlinUtil


import com.alibaba.fastjson2.JSONObject
import com.yulin.kotlinUtil.HttpRequestUtil.Companion.requestJsonPost
import com.yulin.kotlinUtil.RandomUtil.Companion.randomStr

class MihoyoUtil {
    companion object {
        //登录的客户端类型（8是星穹铁道）
        const val appId = "8"

        /**
         * 请求使用二维码登陆，并返回登陆的二维码以及ticket
         * @return array数组，1：二维码链接，2：米哈游二维码ticket，3：虚拟设备信息
         * @author 岚雨凛 <cheng_ying@outlook.com>
         */
        fun miHoYoLiginQRCode(): Array<String> {
            val device = randomStr(64)
            //请求获取登陆ticket以及二维码链接
            val request = requestJsonPost(
                "https://hk4e-sdk.mihoyo.com/hk4e_cn/combo/panda/qrcode/fetch",
                "{\"app_id\":\"${appId}\",\"device\":\"${device}\"}"
            )
            //获取原始JSON内的data
            val data = JSONObject.parseObject(request)["data"].toString()
            //获取二维码链接
            val qrUrl = JSONObject.parseObject(data)["url"].toString().replace("\\u0026", "&")
            //截取ticket
            val ticket = qrUrl.split("icket=")[1]
            println(request)
            return arrayOf(qrUrl, ticket, device)
        }

    }
}