package com.example.imagetextapp.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.imagetextapp.model.Author
import com.example.imagetextapp.model.Post
import kotlin.math.max
import kotlin.math.min

/**
 * 开启实验性API的使用
 * ExperimentalMaterial3Api: 使用Material 3的实验性组件
 * ExperimentalFoundationApi: 使用Compose Foundation库的实验性组件
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailScreen(
    navController: NavController,  // 导航控制器，用于返回等导航操作
    post: Post  // 从上一页传递过来的帖子数据
) {
    // 使用remember记录和响应状态变化
    var isLiked by remember { mutableStateOf(post.is_liked) }  // 点赞状态
    var likeCount by remember { mutableStateOf(post.like_count) }  // 点赞数量
    var isFollowed by remember { mutableStateOf(false) }  // 关注状态
    var isCollected by remember { mutableStateOf(false) }  // 收藏状态

    // 计算图片容器比例 - 修复：使用安全调用
    val aspectRatio = remember(post.clips?.firstOrNull()) {
        val firstClip = post.clips?.firstOrNull()
        if (firstClip != null) {
            val ratio = firstClip.width.toFloat() / firstClip.height.toFloat()
            // 限制在 3:4 ~ 16:9 之间，避免极端比例
            min(max(ratio, 0.75f), 1.777f)
        } else {
            1f  // 默认比例
        }
    }

    // Scaffold是Material Design的基本布局结构，提供TopBar、BottomBar等标准区域
    Scaffold(
        topBar = {  // 顶部应用栏
            TopAuthorSection(
                author = post.author,
                isFollowed = isFollowed,
                onFollowToggle = { isFollowed = !isFollowed },  // 切换关注状态
                onBackClick = { navController.popBackStack() }  // 返回上一页
            )
        },
        bottomBar = {  // 底部交互栏
            BottomInteractionBar(
                isLiked = isLiked,
                likeCount = likeCount,
                isCollected = isCollected,
                onLikeClick = {  // 点赞/取消点赞
                    isLiked = !isLiked
                    likeCount = if (isLiked) likeCount + 1 else likeCount - 1
                },
                onShareClick = { /* 分享功能 */ }  // 分享功能占位
            )
        }
    ) { innerPadding ->  // Scaffold的内容区域，自动处理innerPadding
        Column(  // 垂直布局
            modifier = Modifier
                .fillMaxSize()  // 填充整个可用空间
                .padding(innerPadding)  // 应用Scaffold的内边距
        ) {
            // 图片横滑容器
            ImagePagerSection(
                post = post,
                aspectRatio = aspectRatio
            )

            Spacer(modifier = Modifier.height(16.dp))  // 间距

            // 内容区域
            ContentSection(post = post)
        }
    }
}

/**
 * 开启Material 3实验性API
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAuthorSection(
    author: Author,  // 作者信息
    isFollowed: Boolean,  // 是否已关注
    onFollowToggle: () -> Unit,  // 关注/取消关注回调
    onBackClick: () -> Unit  // 返回回调
) {
    // 顶部应用栏
    TopAppBar(
        title = {  // 标题区域
            Row(
                verticalAlignment = Alignment.CenterVertically,  // 垂直居中
                modifier = Modifier.fillMaxWidth()  // 填充宽度
            ) {
                // 作者头像
                AsyncImage(
                    model = author.avatar,  // 头像URL
                    contentDescription = "作者头像",  // 无障碍描述
                    modifier = Modifier
                        .size(32.dp)  // 头像大小
                        .clip(CircleShape)  // 圆形裁剪
                )
                Spacer(modifier = Modifier.width(12.dp))  // 间距
                Text(
                    text = author.nickname,  // 作者昵称
                    style = MaterialTheme.typography.bodyMedium,  // 字体样式
                    fontWeight = FontWeight.Medium  // 字体粗细
                )
                Spacer(modifier = Modifier.weight(1f))  // 占位符，将后续内容推到右侧

                // 关注按钮
                Button(
                    onClick = onFollowToggle,  // 点击回调
                    colors = ButtonDefaults.buttonColors(
                        // 根据关注状态设置按钮颜色
                        containerColor = if (isFollowed)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(20.dp)  // 圆角形状
                ) {
                    Text(
                        text = if (isFollowed) "已关注" else "关注",  // 按钮文本
                        fontSize = 12.sp  // 字体大小
                    )
                }
            }
        },
        navigationIcon = {  // 导航图标（左侧）
            IconButton(onClick = onBackClick) {  // 返回按钮
                Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
            }
        },
        actions = {  // 操作区域（右侧）
            // 空actions，所有内容都在title中
        }
    )
}

/**
 * 开启Foundation实验性API
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePagerSection(
    post: Post,  // 帖子数据
    aspectRatio: Float  // 图片宽高比
) {
    val clips = post.clips ?: emptyList()  // 安全调用，提供默认值
    val pagerState = rememberPagerState(pageCount = { clips.size })  // 修复：使用安全的clips创建Pager状态

    Box(  // 容器Box
        modifier = Modifier
            .fillMaxWidth()  // 宽度填满
            .aspectRatio(aspectRatio)  // 按计算出的宽高比设置容器
    ) {
        // 水平分页器，支持横向滑动切换图片
        HorizontalPager(
            state = pagerState,  // 分页状态
            modifier = Modifier.fillMaxSize()  // 填充父容器
        ) { page ->
            val clip = clips[page]  // 获取当前页对应的图片数据，修复：使用安全的clips

            // 支持加载状态和失败状态的异步图片加载组件
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)  // 图片请求构建器
                    .data(clip.url)  // 图片URL
                    .crossfade(true)  // 启用淡入淡出效果
                    .build(),
                contentDescription = "作品图片",  // 无障碍描述
                contentScale = ContentScale.Crop,  // 裁剪填充
                modifier = Modifier.fillMaxSize(),  // 填充容器
                loading = {  // 加载中状态
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),  // 背景色
                        contentAlignment = Alignment.Center  // 内容居中
                    ) {
                        CircularProgressIndicator()  // 圆形进度指示器
                    }
                },
                error = {  // 加载失败状态
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.errorContainer),  // 错误背景色
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.Warning,  // 警告图标
                                contentDescription = "加载失败",
                                tint = MaterialTheme.colorScheme.error,  // 错误颜色
                                modifier = Modifier.size(48.dp)  // 图标大小
                            )
                            Spacer(modifier = Modifier.height(8.dp))  // 间距
                            Text(
                                text = "图片加载失败",  // 错误信息
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }

        // 进度条（多图时显示）- 修复：使用安全的clips
        if (clips.size > 1) {
            ProgressIndicator(
                pageCount = clips.size,  // 总页数
                currentPage = pagerState.currentPage,  // 当前页
                modifier = Modifier
                    .align(Alignment.TopCenter)  // 顶部居中
                    .padding(top = 16.dp)  // 顶部间距
            )
        }
    }
}

/**
 * 分页进度指示器
 * @param pageCount 总页数
 * @param currentPage 当前页码
 * @param modifier 修饰符
 */
@Composable
fun ProgressIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center  // 水平居中
    ) {
        repeat(pageCount) { index ->  // 循环创建指示点
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)  // 水平间距
                    .size(
                        width = if (index == currentPage) 20.dp else 8.dp,  // 当前页指示点更宽
                        height = 4.dp
                    )
                    .background(
                        color = if (index == currentPage) Color.White else Color.White.copy(alpha = 0.5f),  // 当前页更亮
                        shape = RoundedCornerShape(2.dp)  // 圆角矩形
                    )
            )
        }
    }
}

/**
 * 内容区域组件
 * @param post 帖子数据
 */
@Composable
fun ContentSection(post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()  // 宽度填满
            .padding(16.dp)  // 内边距
    ) {
        // 标题区 - 完整展示不截断
        Text(
            text = post.title ?: "无标题",  // 标题，为空时显示"无标题"
            style = MaterialTheme.typography.headlineSmall,  // 字体样式
            fontWeight = FontWeight.Bold,  // 字体粗细
            modifier = Modifier.padding(bottom = 12.dp),  // 底部间距
            maxLines = Int.MAX_VALUE  // 不限制行数
        )

        // 正文区 - 支持话题词高亮和点击
        HighlightedText(
            text = post.content,  // 正文内容
            modifier = Modifier.padding(bottom = 16.dp)  // 底部间距
        )

        // 发布日期
        Text(
            text = "发布于 ${formatDetailDate(post.create_time)}",  // 格式化发布时间
            style = MaterialTheme.typography.bodySmall,  // 小号字体
            color = MaterialTheme.colorScheme.onSurfaceVariant  // 次要文本颜色
        )
    }
}

/**
 * 高亮文本组件，支持话题词高亮
 * @param text 原始文本
 * @param modifier 修饰符
 */
@Composable
fun HighlightedText(
    text: String,
    modifier: Modifier = Modifier
) {
    val hashtagRegex = "#[^\\s#]+".toRegex()  // 正则表达式匹配话题词（以#开头，不含空格和#）
    val matches = hashtagRegex.findAll(text)  // 查找所有匹配

    if (matches.none()) {  // 如果没有话题词
        // 没有话题词，直接显示
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,  // 正文字体
            lineHeight = 24.sp,  // 行高
            maxLines = Int.MAX_VALUE  // 不限制行数
        )
        return
    }

    // 构建带样式的文本
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0  // 记录上一次处理的位置

        matches.forEach { matchResult ->
            // 添加普通文本
            append(text.substring(lastIndex, matchResult.range.first))

            // 添加高亮的话题词
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,  // 主题主色
                    fontWeight = FontWeight.Medium  // 中等粗细
                )
            ) {
                append(matchResult.value)  // 添加话题词文本
            }

            lastIndex = matchResult.range.last + 1  // 更新处理位置
        }

        // 添加剩余文本
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }

    Text(
        text = annotatedString,  // 带样式的文本
        style = MaterialTheme.typography.bodyLarge,
        lineHeight = 24.sp,
        maxLines = Int.MAX_VALUE,
        modifier = modifier.clickable {  // 点击事件
            // 话题词点击处理 - 这里可以添加跳转到话题页面的逻辑
        }
    )
}

/**
 * 底部交互栏组件
 * @param isLiked 是否已点赞
 * @param likeCount 点赞数
 * @param isCollected 是否已收藏
 * @param onLikeClick 点赞/取消点赞回调
 * @param onShareClick 分享回调
 */
@Composable
fun BottomInteractionBar(
    isLiked: Boolean,
    likeCount: Int,
    isCollected: Boolean,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()  // 宽度填满
            .padding(16.dp),  // 内边距
        verticalAlignment = Alignment.CenterVertically  // 垂直居中
    ) {
        // 快捷评论框
        OutlinedTextField(
            value = "",  // 输入框值
            onValueChange = { },  // 值变化回调，目前为空
            placeholder = {  // 占位符
                Text(
                    text = "",  // 更明确的占位文本
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),  // 提高可见性
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .weight(1f)  // 占据剩余空间
                .height(40.dp),  // 固定高度
            shape = RoundedCornerShape(20.dp),  // 圆角形状
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,  // 聚焦时背景色
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant  // 未聚焦时背景色
            )
        )

        Spacer(modifier = Modifier.width(12.dp))  // 间距

        // 点赞按钮
        IconButton(onClick = onLikeClick) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {  // 列布局，水平居中
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.Favorite,  // 根据状态选择图标
                    contentDescription = "点赞",
                    tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurface  // 根据状态选择颜色
                )
                Text(
                    text = likeCount.toString(),  // 点赞数
                    style = MaterialTheme.typography.labelSmall  // 小号字体
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))  // 间距

        // 评论按钮
        IconButton(
            onClick = { /* 评论功能 */ },
            enabled = false  // 暂时禁用
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "评论"
                )
                Text("评论", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))  // 间距

        // 收藏按钮
        IconButton(
            onClick = { /* 收藏功能 */ },
            enabled = false  // 暂时禁用
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = if (isCollected) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,  // 根据状态选择图标
                    contentDescription = "收藏"
                )
                Text("收藏", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))  // 间距

        // 分享按钮
        IconButton(onClick = onShareClick) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Share, contentDescription = "分享")
                Text("分享", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

/**
 * 详情页专用的日期格式化函数
 * @param timestamp 时间戳（秒）
 * @return 格式化后的日期字符串
 */
fun formatDetailDate(timestamp: Long): String {
    val currentTime = System.currentTimeMillis() / 1000  // 当前时间戳（秒）
    val diff = currentTime - timestamp  // 时间差（秒）

    return when {
        // 24小时内
        diff < 86400 -> {
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            if (diff < 86400 && diff >= 82800) { // 昨天（23小时-24小时之间）
                "昨天 ${sdf.format(java.util.Date(timestamp * 1000))}"  // 格式：昨天 HH:mm
            } else {
                sdf.format(java.util.Date(timestamp * 1000))  // 格式：HH:mm
            }
        }
        // 7天内
        diff < 604800 -> "${diff / 86400}天前"  // 格式：X天前
        // 其他情况
        else -> {
            val sdf = java.text.SimpleDateFormat("MM-dd", java.util.Locale.getDefault())  // 格式：MM-dd
            sdf.format(java.util.Date(timestamp * 1000))
        }
    }
}