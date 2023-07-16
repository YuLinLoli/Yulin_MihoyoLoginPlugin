import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.8.0"
    kotlin("plugin.serialization") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.15.0"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("com.github.gmazzo.buildconfig") version "4.1.1"
}
val yulinVersion = "1.0.1"
val name = "米哈游登陆插件${yulinVersion}"
group = "yulin"
version = yulinVersion

buildConfig{
    className("BuildConfig")
    packageName("yulin.config")
    buildConfigField("String","yulinVersion","\"${yulinVersion}\"")
    buildConfigField("String","name","\"${name}\"")
    buildConfigField("String","id","\"${group}\"")
}
repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    maven("https://maven.aliyun.com/repository/central")
    mavenCentral()
}
dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("net.mamoe:mirai-core-jvm:2.15.0-M1")
    implementation("top.jfunc.common:converter:1.8.0")


    implementation("com.madgag:animated-gif-lib:1.4")
    compileOnly("org.bytedeco:javacv-platform:1.5.7")
//    compileOnly
    implementation(kotlin("stdlib-jdk8"))
}


val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

