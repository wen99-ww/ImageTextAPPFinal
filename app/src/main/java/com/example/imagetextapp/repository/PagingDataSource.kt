// repository/PagingDataSource.kt
package com.example.imagetextapp.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.imagetextapp.model.Post
import retrofit2.HttpException
import java.io.IOException

// 分页数据源类，用于处理列表数据的分页加载
class PostPagingSource(
    // 远程数据源，用于从服务器获取数据
    private val remoteDataSource: RemoteDataSource
) : PagingSource<Int, Post>() {  // 继承PagingSource，键类型为Int（页码），值类型为Post

    // 添加最大页数限制
    private var maxPages = 0

    // 重写加载方法，核心分页逻辑在此实现
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            // 获取请求的页码，如果是第一次加载则从第0页开始
            val page = params.key ?: 0
            // 获取每页加载数量
            val count = params.loadSize

            // 防止无限加载：设置最大页数为50
            if (page > 50) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            // 记录分页加载日志
            Log.d("PostPagingSource", "Loading page: $page, count: $count")

            // 调用远程数据源获取数据
            val response = remoteDataSource.getFeed(count, page)

            // 检查API响应状态码
            if (response.status_code == 0) {
                val posts = response.post_list

                // 修复：确保posts不为null，避免空指针异常
                val safePosts = posts ?: emptyList()

                Log.d("PostPagingSource", "Loaded ${safePosts.size} posts, has_more: ${response.has_more}")

                // 如果没有数据，停止分页
                if (safePosts.isEmpty()) {
                    Log.d("PostPagingSource", "No more posts available")
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = if (page > 0) page - 1 else null,  // 如果有上一页，设置上一页键
                        nextKey = null  // 没有下一页
                    )
                }

                // 成功加载数据，返回分页结果
                LoadResult.Page(
                    data = safePosts,  // 当前页数据
                    prevKey = if (page > 0) page - 1 else null,  // 计算上一页页码
                    // 修复：只有当有更多数据且不是最后一页时才加载下一页
                    nextKey = if (response.has_more == 1 && safePosts.isNotEmpty()) {
                        page + 1  // 计算下一页页码
                    } else null
                )
            } else {
                // API返回错误状态码
                Log.e("PostPagingSource", "API error: ${response.status_code}")
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (e: Exception) {
            // 捕获并处理异常
            Log.e("PostPagingSource", "Exception: ${e.message}")
            LoadResult.Error(e)  // 返回错误结果
        }
    }

    // 获取刷新键，当数据失效时重新加载
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return null  // 简化刷新逻辑，返回null表示不保存滚动位置
    }
}