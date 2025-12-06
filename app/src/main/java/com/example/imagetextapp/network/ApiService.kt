// network/ApiService.kt
// 定义网络请求相关的接口和服务

// 包声明，指定当前文件在项目中的路径
package com.example.imagetextapp.network

// 导入项目所需的相关类和接口
import com.example.imagetextapp.model.ApiResponse  // 导入数据模型，用于解析网络响应
import retrofit2.Call  // Retrofit的调用类，用于执行网络请求
import retrofit2.http.GET  // Retrofit注解，声明HTTP GET请求
import retrofit2.http.Query  // Retrofit注解，声明查询参数

/**
 * ApiService 接口
 *
 * 使用Retrofit框架定义网络请求接口，所有与服务器交互的API都在此接口中声明
 * Retrofit会根据接口定义和注解自动生成实现代码
 *
 * 接口使用规则：
 * 1. 每个方法代表一个具体的API端点
 * 2. 使用注解声明HTTP方法和参数
 * 3. 返回值通常为Call<T>或Response<T>类型
 * 4. 方法名应清晰表达API的功能
 */
interface ApiService {

    /**
     * 获取信息流/动态列表
     *
     * 这是一个GET请求，用于从服务器获取信息流数据
     * 端点路径为 "feed/"，会追加到Retrofit BaseUrl之后
     * 例如：如果BaseUrl是"https://api.example.com/"，则完整URL是"https://api.example.com/feed/"
     *
     * @param count 请求的数据数量，默认值为6
     *             - 用于控制一次性获取的数据条数
     *             - 服务器端通常会根据此参数进行分页或限制返回数量
     *             - 默认值6表示如果不指定数量，则默认获取6条数据
     *
     * @param acceptVideoClip 是否接受视频片段，默认值为false
     *                       - 用于控制返回的数据是否包含视频内容
     *                       - 当设置为true时，响应中可能包含视频类型的数据
     *                       - 当设置为false时，可能只返回图片或文本内容
     *                       - 这个参数可以帮助过滤内容类型，优化数据传输
     *
     * @return Call<ApiResponse> Retrofit的Call对象，用于异步执行网络请求
     *         - Call对象代表一个准备执行的网络请求
     *         - 可以通过enqueue()方法异步执行，或execute()方法同步执行
     *         - ApiResponse是自定义的响应数据模型类，用于解析JSON响应
     *         - 泛型参数ApiResponse指定了响应数据的解析格式
     *
     * 注解说明：
     * @GET("feed/") - 声明这是一个HTTP GET请求，请求路径为"feed/"
     * @Query("count") - 声明这是一个查询参数，参数名为"count"，会附加在URL后，如"?count=6"
     * @Query("accept_video_clip") - 查询参数，参数名为"accept_video_clip"
     */
    @GET("feed/")
    fun getFeed(
        @Query("count") count: Int = 6,
        @Query("accept_video_clip") acceptVideoClip: Boolean = false
    ): Call<ApiResponse>  // 返回类型为Call，用于后续的网络请求执行
}