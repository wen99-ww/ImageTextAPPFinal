package com.example.imagetextapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.imagetextapp.cache.PostCache
import com.example.imagetextapp.model.Post
import com.example.imagetextapp.ui.screen.DetailScreen
import com.example.imagetextapp.ui.screen.HomeScreen
import com.example.imagetextapp.ui.screen.ProfileScreen

/**
 * 导航路由定义，管理应用中的所有屏幕路由
 * sealed class 确保路由类型的封闭性，防止非法路由
 */
sealed class Screen(val route: String) {
    // 首页路由
    object Home : Screen("home")

    // 个人资料页路由
    object Profile : Screen("profile")

    /**
     * 详情页路由，包含动态参数
     * 路由格式：detail/{postId}，其中postId是动态参数
     */
    object Detail : Screen("detail/{postId}") {
        /**
         * 创建包含帖子ID的完整路由
         * @param postId 帖子唯一标识符
         * @return 格式化的路由字符串
         */
        fun createRoute(postId: String) = "detail/$postId"
    }
}

/**
 * 设置导航图，定义各屏幕的跳转关系
 * @param navController 导航控制器，用于管理导航栈
 */
fun NavGraphBuilder.setupNavigation(navController: NavController) {
    // 首页屏幕
    composable(Screen.Home.route) {
        HomeScreen(navController)  // 修复：传递navController参数，支持从首页跳转到其他页面
    }

    // 个人资料页屏幕
    composable(Screen.Profile.route) {
        ProfileScreen()  // 个人资料页面
    }

    /**
     * 详情页屏幕，包含动态参数解析
     * 通过postId参数从缓存或数据源获取帖子详情
     */
    composable(
        route = Screen.Detail.route,  // 详情页路由
        arguments = listOf(
            navArgument("postId") {  // 定义路由参数
                type = NavType.StringType  // 参数类型为字符串
            }
        )
    ) { backStackEntry ->  // 接收导航返回栈条目
        // 从路由参数中提取postId
        val postId = backStackEntry.arguments?.getString("postId") ?: ""

        // 尝试从缓存获取帖子数据
        val post = PostCache.getPost(postId)

        // 这里需要从数据源获取对应的Post对象
        // val post = getPostById(postId)  // 注释掉的代码，后续需要实现真实数据源

        // 如果找到帖子，显示详情页
        if (post != null) {
            DetailScreen(
                navController = navController,  // 传递导航控制器用于返回操作
                post = post  // 传递帖子数据
            )
        } else {
            // 如果找不到帖子，返回并显示错误
            navController.popBackStack()  // 返回到上一个页面
        }
    }
}

/**
 * 临时函数，用于通过帖子ID获取帖子数据
 * 后续需要从Repository或ViewModel获取真实数据
 * @param postId 帖子的唯一标识符
 * @return 对应ID的帖子对象，当前实现总是返回null
 */
private fun getPostById(postId: String): Post? {
    // TODO: 这里应该从数据源（Repository）获取真实数据
    // 目前返回null，表示尚未实现
    return null
}

/**
 * 错误页面组件，用于显示错误信息
 * 当数据加载失败或页面不存在时显示
 * @param message 要显示的错误信息
 */
@androidx.compose.runtime.Composable
fun ErrorScreen(message: String) {
    // 使用Box容器填充整个屏幕
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),  // 填充整个屏幕
        contentAlignment = androidx.compose.ui.Alignment.Center  // 内容居中对齐
    ) {
        // 显示错误信息文本
        androidx.compose.material3.Text(text = message)
    }
}