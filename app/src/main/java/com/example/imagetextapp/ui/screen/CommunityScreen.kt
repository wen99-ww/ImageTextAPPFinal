package com.example.imagetextapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imagetextapp.ui.component.FeedCard
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.navigation.NavController
import com.example.imagetextapp.cache.PostCache
import com.example.imagetextapp.ui.navigation.Screen

/**
 * 社区页面（主屏幕），显示帖子列表
 * 功能包括：帖子展示、下拉刷新、分页加载、点击查看详情、点赞
 * @param navController 导航控制器，用于页面跳转
 */
@Composable
fun CommunityScreen(navController: NavController) {  // 添加参数
    // 获取CommunityViewModel实例，通过工厂模式创建
    val viewModel: CommunityViewModel = viewModel(
        factory = CommunityViewModelFactory()
    )

    // 从ViewModel中获取分页数据流，转换为LazyPagingItems供Compose使用
    val posts = viewModel.posts.collectAsLazyPagingItems()

    // 获取当前上下文，用于显示Toast
    val context = LocalContext.current
    // 创建协程作用域，用于处理异步操作
    val scope = rememberCoroutineScope()

    // 下拉刷新状态，控制刷新动画
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    // 网格列表状态，记录滚动位置等信息
    val listState = rememberLazyGridState()

    /**
     * 监听刷新状态的变化
     * LaunchedEffect在posts.loadState.refresh变化时执行
     */
    LaunchedEffect(posts.loadState.refresh) {
        when (posts.loadState.refresh) {
            is androidx.paging.LoadState.Loading -> {
                // 如果是用户手动触发的刷新，保持isRefreshing为true
                if (!isRefreshing) {
                    // 这是初始加载，不显示下拉刷新指示器
                }
            }
            is androidx.paging.LoadState.NotLoading -> {
                // 刷新完成，停止刷新动画
                isRefreshing = false
            }
            is androidx.paging.LoadState.Error -> {
                // 刷新出错，停止刷新动画并记录错误日志
                isRefreshing = false
                val error = (posts.loadState.refresh as androidx.paging.LoadState.Error).error
                Log.e("CommunityScreen", "Refresh error: $error")
            }
        }
    }

    /**
     * 监听加载更多状态的变化
     * 当用户滚动到底部加载更多数据时触发
     */
    LaunchedEffect(posts.loadState.append) {
        when (posts.loadState.append) {
            is androidx.paging.LoadState.Error -> {
                // 加载更多出错，记录错误日志
                val error = (posts.loadState.append as androidx.paging.LoadState.Error).error
                Log.e("CommunityScreen", "Append error: $error")
            }
            else -> {}
        }
    }

    /**
     * 下拉刷新容器
     * SwipeRefresh是accompanist库提供的下拉刷新组件
     */
    SwipeRefresh(
        state = swipeRefreshState,  // 刷新状态
        onRefresh = {               // 下拉刷新回调
            isRefreshing = true
            posts.refresh()         // 重新加载数据
        },
        modifier = Modifier.fillMaxSize()  // 填充整个屏幕
    ) {
        // 主内容容器
        Box(modifier = Modifier.fillMaxSize()) {
            /**
             * 垂直网格列表，两列布局
             * LazyVerticalGrid是Compose中的网格布局组件
             */
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),       // 固定2列
                modifier = Modifier.fillMaxSize(),  // 填充容器
                state = listState,                  // 列表状态
                contentPadding = PaddingValues(8.dp)  // 内边距
            ) {
                /**
                 * 网格项，根据数据数量动态生成
                 * count: 总项数
                 * key: 为每项提供唯一键，优化重组性能
                 */
                items(
                    count = posts.itemCount,
                    key = { index ->
                        val post = posts[index]
                        "${post?.post_id ?: "null"}-$index"  // 使用post_id和索引组合为键
                    }
                ) { index ->
                    val post = posts[index]  // 获取当前索引对应的帖子

                    if (post != null) {
                        // 如果帖子不为空，显示FeedCard组件
                        FeedCard(
                            post = post,
                            onItemClick = { clickedPost ->
                                // 点击卡片：缓存帖子并跳转到详情页
                                PostCache.cachePost(clickedPost)
                                navController.navigate(Screen.Detail.createRoute(clickedPost.post_id))
                            },
                            onLikeClick = { likedPost ->
                                // 点击点赞：显示Toast提示
                                Toast.makeText(
                                    context,
                                    if (likedPost.is_liked) "取消点赞" else "点赞成功",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    } else {
                        // 如果帖子为空（数据正在加载），显示加载占位符
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .height(200.dp)
                                .fillMaxWidth()
                        ) {
                            CircularProgressIndicator(  // 圆形进度条
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center)  // 居中显示
                            )
                        }
                    }
                }

                // 加载更多 - 显示底部加载指示器
                if (posts.loadState.append is androidx.paging.LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()  // 宽度填满
                                .padding(16.dp),  // 内边距
                            contentAlignment = Alignment.Center  // 内容居中
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }

                // 加载更多错误 - 显示错误信息和重试按钮
                if (posts.loadState.append is androidx.paging.LoadState.Error) {
                    item {
                        val error = (posts.loadState.append as androidx.paging.LoadState.Error).error
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "加载失败: ${error.message?.take(20) ?: "未知错误"}",  // 截取前20字符
                                    color = Color.Red
                                )
                                Spacer(modifier = Modifier.height(8.dp))  // 间距
                                Button(
                                    onClick = { posts.retry() }  // 重试加载
                                ) {
                                    Text("重试")
                                }
                            }
                        }
                    }
                }
            }

            // 初始加载状态 - 首次加载时显示全屏加载指示器
            if (posts.loadState.refresh is androidx.paging.LoadState.Loading && posts.itemCount == 0) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp))
                }
            }

            // 初始加载错误 - 首次加载失败时显示错误信息和重试按钮
            if (posts.loadState.refresh is androidx.paging.LoadState.Error && posts.itemCount == 0) {
                val error = (posts.loadState.refresh as androidx.paging.LoadState.Error).error
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "加载失败: ${error.message?.take(30) ?: "未知错误"}",
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { posts.refresh() }  // 重新刷新
                        ) {
                            Text("重试")
                        }
                    }
                }
            }

            // 空状态 - 数据加载完成但没有数据时显示
            if (posts.loadState.refresh is androidx.paging.LoadState.NotLoading
                && posts.itemCount == 0) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无数据", color = Color.Gray)
                }
            }
        }
    }
}