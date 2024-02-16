import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.8.0"
    kotlin("plugin.serialization") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.16.0"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("com.github.gmazzo.buildconfig") version "4.1.1"
}

group = "com.yulin"
version = "1.0.5"
buildConfig {
    className("BuildConfig")
    packageName("com.yulin.cg")
    buildConfigField("String", "yulinVersion", "\"${version}\"")
    buildConfigField("String", "name", "\"米哈游登录插件\"")
    buildConfigField("String", "id", "\"com.yulin.MihoyoLoginPlugin\"")
}
repositories {
    mavenLocal()
//    maven("https://maven.aliyun.com/repository/gradle-plugin")
//    maven("https://maven.aliyun.com/repository/central")
    mavenCentral()
}
dependencies {
    implementation("com.google.code.gson:gson:2.9.1")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("net.mamoe:mirai-core-jvm:2.16.0")
    // https://mvnrepository.com/artifact/com.alibaba.fastjson2/fastjson2
    implementation("com.alibaba.fastjson2:fastjson2:2.0.35")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.0")
    // https://mvnrepository.com/artifact/com.google.zxing/core
    implementation("com.google.zxing:core:3.5.1")
    implementation("com.madgag:animated-gif-lib:1.4")
    compileOnly("org.bytedeco:javacv-platform:1.5.7")
    // https://mvnrepository.com/artifact/org.hjson/hjson
    implementation("org.hjson:hjson:3.0.0")
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

