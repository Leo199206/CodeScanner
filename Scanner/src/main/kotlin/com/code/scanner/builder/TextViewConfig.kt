package com.code.scanner.builder

import android.graphics.Color
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.annotation.Dimension

/**
 * <pre>
 *     author : leo
 *     time   : 2020/06/03
 *     desc   : TextView参数配置
 * </pre>
 * @property builder Builder?
 * @property gravity Int
 * @property maxLines Int
 * @property color Int
 * @property size Int
 * @constructor
 */
class TextViewConfig constructor(private var builder: Builder) {

    val gravity: Int
        get() = builder.gravity

    val maxLines: Int
        get() = builder.maxLines

    val color: Int
        get() = builder.color

    val size: Int
        get() = builder.size


    class Builder {
        var gravity = Gravity.CENTER
        var maxLines = 1

        @ColorInt
        var color = Color.BLACK

        @Dimension
        var size = 0
        fun build(): TextViewConfig {
            return TextViewConfig(this)
        }

        fun setGravity(gravity: Int): Builder {
            this.gravity = gravity
            return this
        }

        fun setMaxLines(maxLines: Int): Builder {
            this.maxLines = maxLines
            return this
        }

        fun setColor(@ColorInt color: Int): Builder {
            this.color = color
            return this
        }

        fun setSize(size: Int): Builder {
            this.size = size
            return this
        }
    }
}
