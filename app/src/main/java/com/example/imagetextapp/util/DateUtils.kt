package com.example.imagetextapp.util

// util/DateUtils.kt

import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期时间工具类
 * 提供时间戳格式化的实用方法
 * 采用单例模式（object关键字），全局共享一个实例
 */
object DateUtils {

    /**
     * 将时间戳格式化为相对时间描述
     * 根据与当前时间的差异，返回不同格式的字符串
     *
     * 时间格式规则：
     * - 小于1分钟：显示"刚刚"
     * - 小于1小时：显示"X分钟前"
     * - 小于1天：显示"X小时前"
     * - 小于1周：显示"X天前"
     * - 超过1周：显示具体日期（月-日格式）
     *
     * @param timestamp 时间戳，单位为秒（Unix时间戳格式）
     * @return 格式化后的时间字符串
     *
     * 示例：
     * - 当前时间 12:00:00，时间戳 12:00:10 -> "刚刚"
     * - 当前时间 12:00:00，时间戳 11:30:00 -> "30分钟前"
     * - 当前时间 12:00:00，时间戳 08:00:00 -> "4小时前"
     * - 当前时间 12:00:00，时间戳 5天前 12:00:00 -> "5天前"
     * - 当前时间 12:00:00，时间戳 2023-10-01 12:00:00 -> "10-01"
     */
    fun formatDate(timestamp: Long): String {
        // 获取当前时间戳（秒），System.currentTimeMillis()返回毫秒，除以1000转换为秒
        val currentTime = System.currentTimeMillis() / 1000

        // 计算时间差（秒），当前时间减去传入的时间戳
        val diff = currentTime - timestamp

        // 使用when表达式根据时间差返回不同的格式化结果
        return when {
            // 时间差小于60秒（1分钟），显示"刚刚"
            diff < 60 -> "刚刚"

            // 时间差小于3600秒（1小时），转换为分钟显示
            diff < 3600 -> "${diff / 60}分钟前"

            // 时间差小于86400秒（24小时），转换为小时显示
            diff < 86400 -> "${diff / 3600}小时前"

            // 时间差小于604800秒（7天），转换为天数显示
            diff < 604800 -> "${diff / 86400}天前"

            // 时间差超过7天，显示具体日期（月-日格式）
            else -> {
                // 创建SimpleDateFormat实例，指定日期格式为"月-日"
                // Locale.getDefault()使用系统默认的区域设置，确保日期格式符合用户习惯
                val sdf = SimpleDateFormat("MM-dd", Locale.getDefault())

                // 将时间戳转换为Date对象（需要乘以1000转换为毫秒），然后格式化为字符串
                sdf.format(Date(timestamp * 1000))
            }
        }
    }
}