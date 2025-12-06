package com.example.imagetextapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.imagetextapp.cache.PostCache
import com.example.imagetextapp.model.Post

/**
 * Feed卡片组件，用于显示单个帖子
 * @param post 帖子数据对象
 * @param onItemClick 卡片点击回调函数
 * @param onLikeClick 点赞按钮点击回调函数
 */
@Composable
fun FeedCard(
    post: Post,
    onItemClick: (Post) -> Unit = {},  // 默认空实现
    onLikeClick: (Post) -> Unit = {}   // 默认空实现
) {
    // 主卡片容器
    Card(
        modifier = Modifier
            .fillMaxWidth()                     // 宽度填满父容器
            .aspectRatio(0.75f)                 // 固定宽高比 4:3
            .padding(4.dp)                     // 四周内边距
            .clickable {                       // 点击事件处理
                PostCache.cachePost(post)      // 缓存帖子到内存
                onItemClick(post)              // 触发点击回调
            },
        shape = RoundedCornerShape(8.dp),      // 圆角形状
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)  // 阴影效果
    ) {
        // 垂直布局：图片区域 + 信息区域
        Column {
            // 封面图片区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()             // 宽度填满
                    .weight(1f)                 // 占据剩余空间的权重
            ) {
                // 获取帖子首张图片URL
                val imageUrl = post.clips?.firstOrNull()?.url ?: ""

                // 异步加载图片
                AsyncImage(
                    model = imageUrl,           // 图片URL
                    contentDescription = "作品封面",  // 无障碍描述
                    contentScale = ContentScale.Crop,  // 裁剪填充
                    modifier = Modifier.fillMaxSize()  // 填满容器
                )

                // 无图片时的占位符
                if (imageUrl.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()     // 填满父容器
                            .background(MaterialTheme.colorScheme.surfaceVariant),  // 背景色
                        contentAlignment = Alignment.Center  // 内容居中
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BrokenImage,  // 破损图片图标
                            contentDescription = "无封面",  // 无障碍描述
                            modifier = Modifier.size(48.dp),  // 图标大小
                            tint = MaterialTheme.colorScheme.onSurfaceVariant  // 图标颜色
                        )
                    }
                }
            }

            // 底部信息区域
            Column(
                modifier = Modifier
                    .fillMaxWidth()            // 宽度填满
                    .padding(8.dp)             // 内边距
            ) {
                // 标题（最多显示2行）
                Text(
                    text = post.title ?: post.content,  // 优先显示标题，否则显示内容
                    maxLines = 2,                        // 最大行数
                    overflow = TextOverflow.Ellipsis,    // 超出部分显示省略号
                    style = MaterialTheme.typography.bodyMedium,  // 文字样式
                    modifier = Modifier.padding(bottom = 4.dp)  // 底部间距
                )

                // 作者信息和点赞区域
                Row(
                    verticalAlignment = Alignment.CenterVertically,  // 垂直居中
                    modifier = Modifier.fillMaxWidth()               // 宽度填满
                ) {
                    // 作者头像
                    AsyncImage(
                        model = post.author.avatar,        // 头像URL
                        contentDescription = "作者头像",   // 无障碍描述
                        modifier = Modifier
                            .size(24.dp)                  // 头像大小
                            .clip(CircleShape)            // 圆形裁剪
                    )

                    // 作者昵称
                    Text(
                        text = post.author.nickname,      // 昵称
                        maxLines = 1,                     // 单行显示
                        overflow = TextOverflow.Ellipsis, // 超出部分显示省略号
                        style = MaterialTheme.typography.labelSmall,  // 小号文字
                        modifier = Modifier
                            .weight(1f)                   // 占据剩余空间
                            .padding(horizontal = 8.dp)   // 水平间距
                    )

                    // 点赞区域
                    Row(
                        verticalAlignment = Alignment.CenterVertically  // 垂直居中
                    ) {
                        // 点赞按钮
                        IconButton(
                            onClick = { onLikeClick(post) },  // 点击回调
                            modifier = Modifier.size(20.dp)   // 按钮大小
                        ) {
                            Icon(
                                // 根据点赞状态显示不同图标
                                imageVector = if (post.is_liked) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                                contentDescription = "点赞",  // 无障碍描述
                                // 点赞时红色，否则为默认颜色
                                tint = if (post.is_liked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // 点赞数量
                        Text(
                            text = formatLikeCount(post.like_count),  // 格式化点赞数
                            style = MaterialTheme.typography.labelSmall  // 小号文字
                        )
                    }
                }
            }
        }
    }
}

/**
 * 格式化点赞数量的工具函数
 * @param count 原始点赞数量
 * @return 格式化后的字符串
 */
private fun formatLikeCount(count: Int): String {
    return when {
        count >= 10000 -> "${count / 1000}k"                     // 超过1万显示"k"单位
        count >= 1000 -> "${count / 1000}.${(count % 1000) / 100}k"  // 超过1千显示小数位
        else -> count.toString()                                 // 直接显示
    }
}