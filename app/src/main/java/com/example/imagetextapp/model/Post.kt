/**
 * 数据模型包
 * 包含应用程序中核心业务数据的定义
 * 使用Kotlin数据类(data class)定义不可变数据结构
 */
// model/Post.kt
// 包声明，定义此文件中的所有类属于这个包路径
package com.example.imagetextapp.model

/**
 * 帖子/动态主数据类
 * 使用data class关键字，自动生成equals(), hashCode(), toString(), copy()等方法
 * 表示社交媒体应用中的一条动态/帖子
 *
 * @property post_id 帖子唯一标识符，不可为空，主键
 * @property title 帖子标题，可为空（某些帖子可能没有标题）
 * @property content 帖子正文内容，不可为空
 * @property create_time 创建时间戳（毫秒级），用于排序和显示
 * @property author 作者信息，包含用户ID、昵称、头像等
 * @property clips 多媒体片段列表（图片/视频），可为空，默认空列表
 * @property music 背景音乐信息，可为空
 * @property hashtag 话题标签列表，记录在文本中的位置，可为空
 * @property like_count 点赞数统计，默认0
 * @property is_liked 当前用户是否已点赞，默认false
 */
data class Post(
    val post_id: String,                      // 主键，唯一标识符
    val title: String? = null,                // 可选标题，允许为空
    val content: String,                      // 核心内容，必填项
    val create_time: Long,                    // Unix时间戳，便于排序
    val author: Author,                       // 作者对象，关联用户信息
    val clips: List<Clip>? = emptyList(),     // 多媒体内容列表，默认空列表防止NPE
    val music: Music? = null,                 // 可选背景音乐
    val hashtag: List<Hashtag>? = null,       // 话题标签位置信息
    val like_count: Int = 0,                  // 点赞计数器，默认0
    val is_liked: Boolean = false             // 当前用户点赞状态
) {
    /**
     * 安全获取多媒体片段列表的扩展属性
     * 当clips为null时返回空列表，避免空指针异常
     * 使用场景：需要遍历clips而不需要担心null安全时
     *
     * @return 非空的List<Clip>，保证遍历安全
     */
    val safeClips: List<Clip>
        get() = clips ?: emptyList()  // Elvis操作符，空值时返回空列表
}

/**
 * 作者/用户信息数据类
 * 表示发布帖子的用户基本信息
 *
 * @property user_id 用户唯一标识符
 * @property nickname 用户昵称，用于显示
 * @property avatar 用户头像URL地址
 */
data class Author(
    val user_id: String,   // 用户唯一ID
    val nickname: String,  // 显示名称
    val avatar: String     // 头像图片地址
)

/**
 * 多媒体片段数据类
 * 表示帖子中的图片或视频资源
 *
 * @property type 媒体类型：0-图片，1-视频
 * @property width 媒体宽度（像素）
 * @property height 媒体高度（像素）
 * @property url 媒体资源URL地址
 *
 * 注意：可考虑使用枚举类替代type的魔法数字
 * 例如：enum class MediaType { IMAGE, VIDEO }
 */
data class Clip(
    val type: Int,     // 0:图片, 1:视频 - 建议使用枚举更安全
    val width: Int,    // 原始宽度
    val height: Int,   // 原始高度
    val url: String    // 资源地址
)

/**
 * 背景音乐数据类
 * 表示帖子关联的背景音乐信息
 *
 * @property volume 音量大小（0-100）
 * @property seek_time 音乐开始播放的时间点（毫秒）
 * @property url 音乐文件URL地址
 */
data class Music(
    val volume: Int,      // 音量百分比
    val seek_time: Int,   // 播放起始位置
    val url: String       // 音乐文件地址
)

/**
 * 话题标签数据类
 * 表示在帖子内容中标记的话题位置信息
 *
 * @property start 话题在文本中的起始位置（字符索引）
 * @property end 话题在文本中的结束位置（字符索引）
 *
 * 示例：内容"今天天气真好#天气#"中，#天气#的start=4, end=6
 */
data class Hashtag(
    val start: Int,  // 起始索引（包含）
    val end: Int     // 结束索引（不包含）
)

/**
 * API响应包装类
 * 用于封装从服务器获取的帖子列表数据
 *
 * @property status_code HTTP状态码或业务状态码
 * @property has_more 是否有更多数据：1-有，0-无
 * @property post_list 帖子数据列表
 *
 * 注意：可考虑使用密封类(sealed class)处理不同的API响应状态
 */
data class ApiResponse(
    val status_code: Int,     // 响应状态码
    val has_more: Int,        // 分页标志
    val post_list: List<Post> // 帖子数据列表
)