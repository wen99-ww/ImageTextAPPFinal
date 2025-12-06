// repository/RemoteDataSource.kt
package com.example.imagetextapp.repository

import com.example.imagetextapp.model.ApiResponse
import com.example.imagetextapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

// 远程数据源类，负责与服务器进行网络通信
class RemoteDataSource {
    /**
     * 获取Feed数据的挂起函数
     * @param count 每页请求的数据条数
     * @param page 请求的页码（当前未在API调用中使用，可能需要后续优化）
     * @return 返回ApiResponse对象，包含服务器响应数据
     * @throws IOException 当网络请求失败或API返回错误时抛出异常
     */
    suspend fun getFeed(count: Int, page: Int): ApiResponse {
        // 使用IO调度器在IO线程中执行网络请求
        return withContext(Dispatchers.IO) {
            try {
                // 执行同步网络请求（Retrofit的execute()方法）
                val response: Response<ApiResponse> = RetrofitClient.apiService.getFeed(
                    count = count,
                    acceptVideoClip = false  // 不接收视频片段
                ).execute()  // 同步执行网络请求

                // 检查HTTP响应是否成功（状态码200-299）
                if (response.isSuccessful) {
                    val body = response.body()
                    // 验证响应体不为空且API状态码为0（成功）
                    if (body != null && body.status_code == 0) {
                        body  // 返回成功的API响应
                    } else {
                        // 抛出API业务逻辑错误异常
                        throw IOException("API returned error: ${body?.status_code}")
                    }
                } else {
                    // 抛出HTTP错误异常
                    throw IOException("HTTP error: ${response.code()}")
                }
            } catch (e: IOException) {
                // 重新抛出IO异常（网络连接问题）
                throw e
            } catch (e: Exception) {
                // 捕获其他异常并转换为IO异常
                throw IOException("Network error: ${e.message}")
            }
        }
    }
}