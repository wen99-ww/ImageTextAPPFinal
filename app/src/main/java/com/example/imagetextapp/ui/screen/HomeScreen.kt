package com.example.imagetextapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.imagetextapp.ui.navigation.Screen

/**
 * 开启Material 3实验性API
 * ScrollableTabRow是Material 3的实验性组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    /**
     * 使用remember记录当前选中的Tab索引
     * 默认选中第4个Tab（索引3），即"社区"页面
     * mutableIntStateOf是Int类型的可观察状态
     */
    var selectedTab by remember { mutableIntStateOf(3) } // 默认选中社区（索引3）

    // 定义顶部Tab的标题列表
    val tabs = listOf("北京", "团购", "关注", "社区", "推荐")

    /**
     * 主页采用垂直布局
     * Column: 垂直排列的布局容器
     * Modifier.fillMaxSize(): 填充整个父容器
     */
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部Tab栏
        ScrollableTabRow(
            selectedTabIndex = selectedTab,  // 当前选中的Tab索引
            modifier = Modifier.fillMaxWidth(),  // 宽度填满
            containerColor = MaterialTheme.colorScheme.background,  // 背景色使用主题背景色
            contentColor = MaterialTheme.colorScheme.primary,  // 内容颜色使用主题主色
            edgePadding = 0.dp  // 边缘内边距设为0
        ) {
            /**
             * 遍历Tab标题列表，为每个标题创建Tab
             * forEachIndexed提供索引和值
             */
            tabs.forEachIndexed { index, title ->
                /**
                 * Tab组件，表示可点击的标签页
                 * selected: 当前Tab是否被选中
                 * onClick: 点击事件，点击时更新selectedTab
                 * text: Tab显示的文本
                 */
                Tab(
                    selected = selectedTab == index,  // 判断当前Tab是否被选中
                    onClick = { selectedTab = index },  // 点击时更新选中状态
                    text = {
                        Text(
                            text = title,  // Tab标题文本
                            style = MaterialTheme.typography.labelLarge,  // 字体样式
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal  // 选中时字体加粗
                        )
                    }
                )
            }

            /**
             * 搜索图标按钮，位于Tab栏最右侧
             * IconButton: 图标按钮，点击时有涟漪效果
             */
            IconButton(
                onClick = { /* 搜索功能，暂未实现 */ },
                modifier = Modifier
                    .size(48.dp)  // 固定大小
                    .padding(8.dp)  // 内边距
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,  // 搜索图标
                    contentDescription = "搜索",  // 无障碍描述
                    tint = MaterialTheme.colorScheme.primary  // 使用主题主色
                )
            }
        }

        /**
         * 内容区域容器
         * Box: 相对布局容器，可以将子元素相对于容器定位
         * weight(1f): 占据Column中剩余的所有空间
         */
        Box(
            modifier = Modifier
                .fillMaxSize()  // 填充剩余空间
                .weight(1f)  // 权重为1，在垂直布局中占据剩余高度
        ) {
            /**
             * 根据选中的Tab显示不同的页面
             * when表达式：类似于switch语句
             */
            when (selectedTab) {
                3 -> CommunityScreen(navController = navController) // 社区页面（索引3）
                else -> PlaceholderScreen("${tabs[selectedTab]}页面开发中...")  // 其他页面显示占位符
            }
        }
    }
}

/**
 * 占位符页面组件
 * 用于显示未实现的页面
 * @param text 要在占位符页面显示的文本
 */
@Composable
fun PlaceholderScreen(text: String) {
    /**
     * Box容器，填充整个父容器
     * contentAlignment: 子元素对齐方式，此处设置为居中对齐
     */
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        /**
         * Text组件，显示传入的文本
         * MaterialTheme.typography.headlineMedium: 中等标题字体样式
         */
        Text(text = text, style = MaterialTheme.typography.headlineMedium)
    }
}