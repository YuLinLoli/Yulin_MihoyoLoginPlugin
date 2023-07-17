package com.yulin

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin

import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.GroupTempMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import com.yulin.config.BuildConfig
import com.yulin.main.MihoyoLoginQRcode.Companion.qrCodeMain


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
            qrCodeMain(this)
        }
        //群临时会话消息
        globalEventChannel().subscribeAlways<GroupTempMessageEvent> {
            qrCodeMain(this)
        }
    }

}



