package com.example.imagetextapp.ui.screen

// ui/screen/ProfileScreen.kt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * 个人资料页面组件
 * 显示用户的基本信息，包括头像、昵称、ID、统计数据等
 * 这是一个静态页面，实际应用中需要从后端获取用户数据
 */
@Composable
fun ProfileScreen() {
    /**
     * 使用Column垂直布局容器
     * fillMaxSize(): 填充整个屏幕
     * padding(16.dp): 16dp的内边距
     * Alignment.CenterHorizontally: 子组件水平居中对齐
     */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 头像区域
        AsyncImage(
            model = "https://picsum.photos/200/200",  // 使用Lorem Picsum随机头像图片URL
            contentDescription = "用户头像",  // 无障碍描述，供屏幕阅读器使用
            modifier = Modifier
                .size(120.dp)  // 设置头像大小为120dp
                .clip(CircleShape)  // 将头像裁剪为圆形
        )

        Spacer(modifier = Modifier.height(16.dp))  // 添加16dp的垂直间距

        // 用户名显示
        Text(
            text = "用户昵称",  // 用户名文本，实际应用中应从用户数据获取
            style = MaterialTheme.typography.headlineMedium,  // 使用主题中的中等标题字体样式
            fontWeight = FontWeight.Bold  // 字体加粗
        )

        Spacer(modifier = Modifier.height(8.dp))  // 添加8dp的垂直间距

        // 用户ID显示
        Text(
            text = "ID: 123456",  // 用户ID文本，实际应用中应从用户数据获取
            style = MaterialTheme.typography.bodyMedium,  // 使用主题中的正文字体样式
            color = MaterialTheme.colorScheme.onSurfaceVariant  // 使用次要文本颜色
        )

        Spacer(modifier = Modifier.height(24.dp))  // 添加24dp的垂直间距

        /**
         * 用户统计数据行
         * fillMaxWidth(): 宽度填满父容器
         * Arrangement.SpaceEvenly: 子元素在容器中均匀分布，每个子元素周围有相等的空间
         */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 作品数量统计
            ProfileStatItem(count = "128", label = "作品")
            // 粉丝数量统计
            ProfileStatItem(count = "1.2K", label = "粉丝")
            // 关注数量统计
            ProfileStatItem(count = "356", label = "关注")
        }

        Spacer(modifier = Modifier.height(32.dp))  // 添加32dp的垂直间距

        // 编辑资料按钮
        Button(
            onClick = { /* 编辑资料 - 点击事件处理，实际应用中应跳转到编辑资料页面 */ },
            modifier = Modifier.fillMaxWidth(0.8f)  // 宽度为父容器的80%
        ) {
            Text("编辑资料")  // 按钮文本
        }
    }
}

/**
 * 个人资料统计数据项组件
 * 用于显示单个统计数据，包括数值和标签
 *
 * @param count 统计数值，以字符串形式传入（支持格式如"1.2K"）
 * @param label 统计项标签，如"作品"、"粉丝"、"关注"
 */
@Composable
fun ProfileStatItem(count: String, label: String) {
    /**
     * 使用Column垂直布局容器
     * Alignment.CenterHorizontally: 子组件水平居中对齐
     */
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 统计数值显示
        Text(
            text = count,  // 统计数值
            style = MaterialTheme.typography.titleLarge,  // 使用主题中的大标题字体样式
            fontWeight = FontWeight.Bold  // 字体加粗
        )

        // 统计标签显示
        Text(
            text = label,  // 统计项标签
            style = MaterialTheme.typography.bodyMedium,  // 使用主题中的正文字体样式
            color = MaterialTheme.colorScheme.onSurfaceVariant  // 使用次要文本颜色
        )
    }
}