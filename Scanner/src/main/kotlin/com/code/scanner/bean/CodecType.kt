package com.code.scanner.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * <pre>
 *     author : kirito
 *     time   : 2020/05/26
 *     desc   : 解码类型
 * </pre>
 *
 * @property value Int
 * @constructor
 * @property QR 扫描二维码
 * @property BAR 扫描条形码
 * @property ALL 扫描全部类型码
 * @property CUSTOM 扫描用户定义类型码
 */
@Parcelize
enum class CodecType(var value: Int) : Parcelable ,Serializable{
    QR(1),
    BAR(2),
    ALL(3),
    CUSTOM(4);

    companion object {
        /**
         * value parse ScannerType
         * @param value Int
         * @return ScannerType
         */
        @JvmStatic
        fun parse(value: Int): CodecType {
            return values().singleOrNull { it.value == value } ?: QR
        }
    }
}
