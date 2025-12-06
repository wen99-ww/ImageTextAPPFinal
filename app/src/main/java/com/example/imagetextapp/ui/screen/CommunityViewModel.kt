package com.example.imagetextapp.ui.screen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.imagetextapp.model.Post
import com.example.imagetextapp.repository.PostRepository
import kotlinx.coroutines.flow.Flow

/**
 * 社区页面ViewModel，负责管理帖子列表数据的加载和状态
 * ViewModel用于在配置变更（如屏幕旋转）时保持数据，避免数据丢失
 *
 * @param repository 帖子数据仓库，用于获取帖子数据
 */
class CommunityViewModel(
    private val repository: PostRepository
) : ViewModel() {

    /**
     * 帖子数据流，通过PagingData实现分页加载
     * 特点：
     * 1. 使用Flow实现响应式数据流
     * 2. 支持分页加载，避免一次性加载所有数据
     * 3. cachedIn()确保在ViewModel生命周期内共享数据流，避免重复加载
     */
    val posts: Flow<PagingData<Post>> = repository.getPosts()
        .cachedIn(viewModelScope)  // 关键：避免重复加载
    // cachedIn()的作用：
    // 1. 将Flow缓存到viewModelScope中
    // 2. 确保在ViewModel存续期间只创建一个数据流
    // 3. 当有多个收集者时共享同一个数据流
    // 4. 避免因屏幕旋转等配置变更导致数据重新加载
}

/**
 * CommunityViewModel的工厂类，用于创建ViewModel实例
 * 实现ViewModelProvider.Factory接口，支持依赖注入
 *
 * 为什么要使用Factory：
 * 1. ViewModel的构造函数可能有参数，需要Factory来创建
 * 2. 在Compose中通过viewModel(factory = ...)使用
 * 3. 便于测试，可以注入不同的依赖
 */
class CommunityViewModelFactory : ViewModelProvider.Factory {

    /**
     * 创建ViewModel实例
     *
     * @param modelClass 要创建的ViewModel类
     * @return 创建的ViewModel实例
     * @throws IllegalArgumentException 当请求的ViewModel类不是CommunityViewModel时抛出异常
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 检查请求的类是否是CommunityViewModel或其子类
        if (modelClass.isAssignableFrom(CommunityViewModel::class.java)) {
            // 创建CommunityViewModel实例，注入PostRepository依赖
            return CommunityViewModel(
                repository = PostRepository()  // 创建PostRepository实例
            ) as T  // 类型转换，确保返回正确的类型
        }
        // 如果请求的ViewModel类不是CommunityViewModel，抛出异常
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}