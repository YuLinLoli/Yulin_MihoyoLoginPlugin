package com.yulin

import com.yulin.cg.BuildConfig
import com.yulin.main.MiHoYoSendMessage.Companion.qrCodeLogin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupTempMessageEvent
import net.mamoe.mirai.event.globalEventChannel


object MihoyoLoginPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = BuildConfig.id,
        name = BuildConfig.name,
        version = BuildConfig.yulinVersion
    )
) {

    override fun onEnable() {
        //监听私聊消息
        globalEventChannel().subscribeAlways<FriendMessageEvent> {
            qrCodeLogin(this)
        }
        //群临时会话消息
        globalEventChannel().subscribeAlways<GroupTempMessageEvent> {
            qrCodeLogin(this)
        }
    }

}



