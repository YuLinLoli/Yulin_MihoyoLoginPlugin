package com.yulin.main

import com.yulin.main.MihoyoLoginQRcode.Companion.qrCodeMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.ForwardMessage
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.RawForwardMessage

class MiHoYoSendMessage {
    companion object {
        suspend fun qrCodeLogin(
            event: MessageEvent
        ) {
            //查看是否匹配指令
            if (!event.message.contentToString().contains("米哈游登录")
                && !event.message.contentToString().contains("米哈游登陆")
            ) {
                return
            }
            val data = qrCodeMain(event)
            val nodes = mutableListOf<ForwardMessage.Node>()
            nodes.add(
                ForwardMessage.Node(
                    senderId = event.bot.id,
                    senderName = event.bot.nameCardOrNick,
                    time = System.currentTimeMillis().toInt(),
                    message = PlainText(
                        "ltoken=${data?.get("token")};ltuid=${data?.get("aid")};cookie_token=${
                            data?.get(
                                "cookieToken"
                            )
                        }"
                    )
                )
            )
            nodes.add(
                ForwardMessage.Node(
                    senderId = event.bot.id,
                    senderName = event.bot.nameCardOrNick,
                    time = System.currentTimeMillis().toInt(),
                    message = PlainText("stoken=${data?.get("token")};stuid=${data?.get("aid")};mid=${data?.get("mid")}")
                )
            )
            nodes.add(
                ForwardMessage.Node(
                    senderId = event.bot.id,
                    senderName = event.bot.nameCardOrNick,
                    time = System.currentTimeMillis().toInt(),
                    message = PlainText("登录完成，以上分别是 Cookie 和 Stoken，分别把两条消息逐条转发给 Bot 完成绑定!")
                )
            )
            println("MiHoYoLogin:成功执行token绑定！")
            val forward = RawForwardMessage(nodes).render(object : ForwardMessage.DisplayStrategy {
                override fun generateTitle(forward: RawForwardMessage): String {
                    return "登录二维码"
                }

                override fun generateSummary(forward: RawForwardMessage): String {
                    return "点击扫码登录"
                }
            })

            event.subject.sendMessage(forward)
        }

        suspend fun miHoYoDrawCardUrl(event: MessageEvent) {
            val data = qrCodeMain(event)
            val ck = "ltoken=${data?.get("token")};ltuid=${data?.get("aid")};cookie_token=${data?.get("cookieToken")}"
            val url = MiHoYoDrawCardUrl.miHoYoDrawCardUrlMain(ck)
            if (url != null) {
                event.subject.sendMessage("抽卡链接获取成功！请把以下链接转发给机器人！")
                withContext(Dispatchers.IO) {
                    Thread.sleep(1000)
                }
                event.subject.sendMessage(url)
            }
        }
    }
}