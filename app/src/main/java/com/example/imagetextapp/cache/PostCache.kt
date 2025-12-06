package com.example.imagetextapp.cache

// 导入Post数据模型类
import com.example.imagetextapp.model.Post

/**
 * 帖子缓存管理器 - 单例对象
 *
 * 使用Kotlin的object关键字创建单例，确保全局只有一个缓存实例
 * 采用内存缓存策略，通过Map数据结构存储帖子数据
 * 主要用于减少对数据库/网络请求的频率，提升应用性能
 */
object PostCache {

    /**
     * 帖子缓存存储容器
     *
     * 使用MutableMap实现键值对存储：
     * - Key: 帖子ID (String类型)
     * - Value: 帖子对象 (Post类型)
     *
     * 注意：这是内存缓存，应用重启后数据会丢失
     * 使用private修饰符确保外部不能直接访问，只能通过提供的公共方法操作
     */
    private val postCache = mutableMapOf<String, Post>()

    /**
     * 缓存帖子对象
     *
     * 将帖子存储到内存缓存中，如果已存在相同ID的帖子，则会覆盖旧值
     *
     * @param post 要缓存的帖子对象，不能为null
     * 使用帖子ID作为键，确保每个帖子在缓存中有唯一标识
     */
    fun cachePost(post: Post) {
        // 将帖子以ID为键存入缓存Map
        // 如果postId已存在，会更新对应的值（覆盖旧数据）
        postCache[post.post_id] = post
    }

    /**
     * 根据帖子ID获取缓存的帖子
     *
     * 从内存缓存中查找指定ID的帖子，如果不存在则返回null
     *
     * @param postId 要查找的帖子ID
     * @return 找到的帖子对象，如果未找到则返回null
     * 返回类型为可空的Post?，调用方需要处理null情况
     */
    fun getPost(postId: String): Post? {
        // 通过键（postId）从Map中获取值（Post对象）
        // 如果键不存在，返回null
        return postCache[postId]
    }

    /**
     * 清空所有缓存
     *
     * 移除缓存中的所有帖子数据，释放内存
     * 通常在以下情况调用：
     * 1. 用户退出登录
     * 2. 应用内存不足
     * 3. 需要强制刷新所有数据
     */
    fun clear() {
        // 调用MutableMap的clear()方法清空所有键值对
        postCache.clear()
    }

    /**
     * 获取当前缓存大小
     * 注释：如果需要监控缓存使用情况，可以添加此方法
     */
     fun getCacheSize(): Int {
         return postCache.size
     }

    /**
     * 检查帖子是否已缓存
     * 注释：如果需要检查某个帖子是否在缓存中，可以添加此方法
     */
     fun containsPost(postId: String): Boolean {
         return postCache.containsKey(postId)
     }
}