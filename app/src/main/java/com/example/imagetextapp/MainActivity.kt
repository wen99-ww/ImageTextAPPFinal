package com.example.imagetextapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.imagetextapp.ui.navigation.Screen
import com.example.imagetextapp.ui.navigation.setupNavigation
import com.example.imagetextapp.ui.screen.HomeScreen
import com.example.imagetextapp.ui.screen.ProfileScreen
import com.example.imagetextapp.ui.theme.ImageTextAppTheme

/**
 * 主Activity，应用的入口点
 * 继承自ComponentActivity，使用Jetpack Compose构建UI
 */
class MainActivity : ComponentActivity() {
    /**
     * Activity创建时调用的生命周期方法
     * @param savedInstanceState 保存实例状态的Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置Activity的内容视图为Compose UI
        setContent {
            // 应用自定义主题
            ImageTextAppTheme {
                // 主应用组件
                MainApp()
            }
        }
    }
}

/**
 * 开启Material 3实验性API
 * MainApp是应用的根组件，管理整体导航结构
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    // 创建导航控制器，管理页面导航栈
    val navController = rememberNavController()
    // 监听当前导航栈条目的变化
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    // 获取当前路由路径
    val currentRoute = currentBackStackEntry?.destination?.route

    // 根据当前路由确定选中的Tab
    val selectedTab = when (currentRoute) {
        Screen.Home.route -> 0        // 首页
        "friends" -> 1                // 朋友页面
        "camera" -> 2                 // 相机页面
        "messages" -> 3               // 消息页面
        Screen.Profile.route -> 4     // 个人资料页面
        else -> 0                     // 默认选中首页
    }

    // Scaffold是Material Design的基本布局结构
    Scaffold(
        bottomBar = {  // 底部导航栏
            // 只在特定路由显示底部导航栏
            if (shouldShowBottomBar(navController)) {
                BottomNavigationBar(
                    selectedTab = selectedTab,  // 当前选中的Tab索引
                    onTabSelected = { tabIndex ->  // Tab点击回调
                        when (tabIndex) {
                            0 -> {  // 首页
                                navController.navigate(Screen.Home.route) {
                                    // 清除回退栈，只保留Home页面
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                            1 -> {  // 朋友页面
                                navController.navigate("friends") {
                                    // 跳转到朋友页面
                                    launchSingleTop = true  // 单例模式，避免重复创建
                                }
                            }
                            2 -> {  // 相机页面
                                navController.navigate("camera") {
                                    // 跳转到相机页面
                                    launchSingleTop = true
                                }
                            }
                            3 -> {  // 消息页面
                                navController.navigate("messages") {
                                    // 跳转到消息页面
                                    launchSingleTop = true
                                }
                            }
                            4 -> {  // 个人资料页面
                                navController.navigate(Screen.Profile.route) {
                                    // 跳转到我的页面
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->  // Scaffold的内容区域，接收内边距参数
        // 导航宿主，管理页面导航
        NavigationHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)  // 应用Scaffold的内边距
        )
    }
}

/**
 * 导航宿主组件，管理所有页面的导航路由
 * @param navController 导航控制器
 * @param modifier 修饰符
 */
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // NavHost是导航的容器，管理页面切换
    NavHost(
        navController = navController,  // 导航控制器
        startDestination = Screen.Home.route,  // 起始路由为首页
        modifier = modifier.fillMaxSize()  // 填充整个容器
    ) {
        // 设置已定义的导航路由（来自navigation包）
        setupNavigation(navController)

        // 添加其他页面的路由
        composable("friends") {  // 朋友页面路由
            PlaceholderScreen("朋友页面开发中...")
        }
        composable("camera") {  // 相机页面路由
            PlaceholderScreen("相机页面开发中...")
        }
        composable("messages") {  // 消息页面路由
            PlaceholderScreen("消息页面开发中...")
        }
    }
}

/**
 * 判断是否应该显示底部导航栏
 * 在详情页等二级页面不显示底部导航栏
 * @param navController 导航控制器
 * @return Boolean 是否显示底部导航栏
 */
@Composable
fun shouldShowBottomBar(navController: NavController): Boolean {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // 在这些页面不显示底部导航栏
    val hideBottomBarRoutes = listOf(
        "detail",  // 详情页
        // 可以添加其他不需要底部导航栏的页面
    )

    // 检查当前路由是否包含需要隐藏底部栏的路由
    return hideBottomBarRoutes.none { route ->
        currentRoute?.contains(route) == true
    }
}

/**
 * 底部导航栏组件
 * @param selectedTab 当前选中的Tab索引
 * @param onTabSelected Tab点击回调函数
 */
@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    // Material 3的底部导航栏组件
    NavigationBar {
        // 定义底部导航项
        val navItems = listOf(
            NavItem("首页", Icons.Filled.Home, 0),
            NavItem("朋友", Icons.Filled.Person, 1),
            NavItem("相机", Icons.Filled.Add, 2),  // 通常相机图标为Add，表示创建新内容
            NavItem("消息", Icons.Filled.Email, 3),
            NavItem("我", Icons.Filled.Person, 4)  // 与朋友页面图标相同，但索引不同
        )

        // 遍历所有导航项，创建NavigationBarItem
        navItems.forEach { item ->
            NavigationBarItem(
                selected = selectedTab == item.index,  // 是否选中当前项
                onClick = {
                    onTabSelected(item.index)  // 点击时触发回调
                },
                enabled = true,  // 启用点击
                icon = {  // 图标
                    Icon(
                        item.icon,
                        contentDescription = item.title,  // 无障碍描述
                        tint = if (selectedTab == item.index)
                            MaterialTheme.colorScheme.primary  // 选中时使用主题主色
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)  // 未选中时使用半透明色
                    )
                },
                label = {  // 文本标签
                    Text(
                        item.title,
                        color = if (selectedTab == item.index)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            )
        }
    }
}

/**
 * 占位符页面，用于显示未完成的页面
 * @param text 要显示的占位文本
 */
@Composable
fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)  // 半透明文本
        )
    }
}

/**
 * 导航项数据类
 * @param title 导航项标题
 * @param icon 导航项图标
 * @param index 导航项索引
 */
data class NavItem(
    val title: String,
    val icon: ImageVector,
    val index: Int
)

/**
 * 预览函数，用于Android Studio的Design工具中预览UI
 */
@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    MainApp()
}