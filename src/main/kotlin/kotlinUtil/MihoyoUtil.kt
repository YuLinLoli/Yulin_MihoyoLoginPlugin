package com.yulin.kotlinUtil


import com.yulin.kotlinUtil.HttpRequestUtil.Companion.requestJsonPost
import com.yulin.kotlinUtil.RandomUtil.Companion.randomStr

class MihoyoUtil {
    companion object{
        val appId = "8"
        val publicKey = "-----BEGIN PUBLIC KEY-----" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDvekdPMHN3AYhm/vktJT+YJr7cI5DcsNKqdsx5DZX0gDuWFuIjzdwButrIYPNmRJ1G8ybDIF7oDW2eEpm5sMbL9zs" +
                "9ExXCdvqrn51qELbqj0XxtMTIpaCHFSI50PfPpTFV9Xt/hmyVwokoOXFlAEgCn+Q" +
                "CgGs52bFoYMtyi+xEQIDAQAB" +
                "-----END PUBLIC KEY-----"
        //请求使用二维码登陆，并返回登陆的二维码以及ticket
        suspend fun miHoYoLiginQRCode(): Array<String>{
            val device = randomStr(64)
            //请求获取登陆ticket
            val request = requestJsonPost(
                "https://hk4e-sdk.mihoyo.com/hk4e_cn/combo/panda/qrcode/fetch",
                "{\"app_id\":\"${appId}\",\"device\":\"${device}\"}"
            )

            val qrUrl = request!!.split("{\"url\":\"")[1].split("\"}")[0].replace("\\u0026","&")

            val ticket = qrUrl.split("icket=")[1]
            return arrayOf(qrUrl,ticket,device)
        }

    }
}