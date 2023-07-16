package yulin

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin

import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import yulin.config.BuildConfig


object MihoyoLoginPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = BuildConfig.id,
        name = BuildConfig.name,
        version = BuildConfig.yulinVersion
    )
) {

    override fun onEnable() {
        //监听群消息
        globalEventChannel().subscribeAlways<GroupMessageEvent> {

        }

        //监听私聊消息
        globalEventChannel().subscribeAlways<FriendMessageEvent> {

        }
    }

}



