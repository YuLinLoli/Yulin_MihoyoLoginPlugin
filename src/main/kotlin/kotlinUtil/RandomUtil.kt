package com.yulin.kotlinUtil

import org.apache.commons.lang3.RandomStringUtils

class RandomUtil {
    companion object {
        /**
         * 获取指定长度随机字符串
         * @param n 随机字符串长度
         * @return 指定长度的随机字符串或者直接报错！
         */
        fun randomStr(n: Int): String {
            return try {
                RandomStringUtils.randomAlphanumeric(n)
            } catch (e: Exception) {
                e.printStackTrace()
                println("生成随机字符串错误!")
                "error"
            }
        }
    }
}