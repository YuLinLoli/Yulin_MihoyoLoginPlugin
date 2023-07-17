package yulin.kotlinUtil

import org.apache.commons.lang3.RandomStringUtils

class RandomUtil {
    companion object{
        suspend fun randomStr(n: Int): String{
            return try {
                RandomStringUtils.randomAlphanumeric(n)
            }catch (e: Exception){
                e.printStackTrace()
                println("生成随机字符串错误!长度为${n}")
                "error"
            }
        }
    }
}