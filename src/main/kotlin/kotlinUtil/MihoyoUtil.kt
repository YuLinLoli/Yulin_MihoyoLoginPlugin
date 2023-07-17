package yulin.kotlinUtil

import com.alibaba.fastjson.JSON
import net.mamoe.mirai.event.events.FriendMessageEvent
import yulin.kotlinUtil.HttpRequestUtil.Companion.requestJsonPost
import yulin.kotlinUtil.RandomUtil.Companion.randomStr

class MihoyoUtil {
    companion object{
        val appId = "8"
        val publicKey = "-----BEGIN PUBLIC KEY-----" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDvekdPMHN3AYhm/vktJT+YJr7cI5DcsNKqdsx5DZX0gDuWFuIjzdwButrIYPNmRJ1G8ybDIF7oDW2eEpm5sMbL9zs" +
                "9ExXCdvqrn51qELbqj0XxtMTIpaCHFSI50PfPpTFV9Xt/hmyVwokoOXFlAEgCn+Q" +
                "CgGs52bFoYMtyi+xEQIDAQAB" +
                "-----END PUBLIC KEY-----"
        suspend fun miHoYoLiginQRCode(){
            val device = randomStr(64)
            //请求获取登陆ticket
            val request = requestJsonPost(
                "https://hk4e-sdk.mihoyo.com/hk4e_cn/combo/panda/qrcode/fetch",
                "{\"app_id\":\"${appId}\",\"device\":\"${device}\"}"
            )

            println(request)
            val url = request!!.split("{\"url\":\"")[1].split("\"}")[0]
            println(url)
            val ticket = url.split("icket=")[1]
            println(ticket)
        }
    }
}