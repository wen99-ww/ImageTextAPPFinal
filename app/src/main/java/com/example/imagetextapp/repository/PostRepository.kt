// repository/PostRepository.kt
package com.example.imagetextapp.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.imagetextapp.model.Post
import kotlinx.coroutines.flow.Flow

// 帖子数据仓库类，负责管理帖子数据的分页加载
class PostRepository(
    // 远程数据源依赖注入，提供默认实现
    private val remoteDataSource: RemoteDataSource = RemoteDataSource()
) {
    /**
     * 获取帖子数据流，支持分页加载
     * @return 返回包含帖子数据的Flow流，用于观察分页数据变化
     */
    fun getPosts(): Flow<PagingData<Post>> {
        // 创建Pager分页器，配置分页参数和数据源
        return Pager(
            // 配置分页参数
            config = PagingConfig(
                pageSize = 6,  // 每页加载6条数据
                enablePlaceholders = false,  // 禁用占位符，提高性能
                initialLoadSize = 6,  // 初始加载大小，与pageSize保持一致
                prefetchDistance = 2  // 预加载距离，提前2页开始加载下一页数据
            ),
            // 分页数据源工厂，创建PostPagingSource实例
            pagingSourceFactory = { PostPagingSource(remoteDataSource) }
        ).flow  // 返回Flow流，便于观察数据变化
    }
}