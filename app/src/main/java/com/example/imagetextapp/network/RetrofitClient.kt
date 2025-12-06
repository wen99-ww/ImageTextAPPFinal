// network/RetrofitClient.kt
package com.example.imagetextapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit网络请求客户端单例对象
 * 使用object关键字声明为单例，确保整个应用中使用同一个实例
 */
object RetrofitClient {
    // 基础URL - 需要替换为实际的服务器地址
    private const val BASE_URL = "https://college-training-camp.bytedance.com/"

    /**
     * 配置OkHttp客户端
     * 设置连接、读取和写入的超时时间均为10秒
     * 用于处理网络请求的底层通信
     */
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS) // 连接超时时间
        .readTimeout(10, TimeUnit.SECONDS)    // 读取数据超时时间
        .writeTimeout(10, TimeUnit.SECONDS)   // 写入数据超时时间
        .build()

    /**
     * 创建Retrofit实例
     * 配置基础URL、HTTP客户端和JSON转换器
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)                    // 设置API基础地址
        .client(client)                       // 设置自定义的OkHttpClient
        .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器，用于JSON序列化/反序列化
        .build()

    /**
     * 创建ApiService接口的实例
     * 通过Retrofit动态生成ApiService接口的实现
     * 供外部直接调用进行网络请求
     */
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}