package com.code.scanner.helper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import com.code.zbar.ImageScanner


/**
 * 将两个Bitmap合并成一个
 *
 * @param first
 * @param second
 * @param fromPoint 第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
 * @return
 */
fun mixtureBitmap(first: Bitmap, second: Bitmap, fromPoint: PointF): Bitmap? {
    val width = first.width.coerceAtLeast(second.width)
    val newBitmap =
        Bitmap.createBitmap(width, first.height + second.height, Bitmap.Config.ARGB_8888)
    val cv = Canvas(newBitmap)
    cv.drawBitmap(first, 0f, 0f, null)
    cv.drawBitmap(second, fromPoint.x, fromPoint.y, null)
    cv.save()
    cv.restore()
    return newBitmap
}

/**
 * 设置水印图片到中间
 *
 * @param src
 * @param watermark
 * @return
 */
fun createWaterMaskCenter(src: Bitmap, watermark: Bitmap): Bitmap? {
    return createWaterMaskBitmap(
        src, watermark,
        (src.width - watermark.width) / 2,
        (src.height - watermark.height) / 2
    )
}

/**
 * 设置水印图片到中间
 *
 * @param src
 * @param watermark
 * @return
 */
fun createWaterMaskBitmap(
    src: Bitmap,
    watermark: Bitmap,
    paddingLeft: Int,
    paddingTop: Int
): Bitmap {
    val width = src.width
    val height = src.height
    val newBitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) // 创建一个新的和SRC长度宽度一样的位图
    val canvas = Canvas(newBitmap)
    canvas.drawBitmap(src, 0f, 0f, null)
    canvas.drawBitmap(watermark, paddingLeft.toFloat(), paddingTop.toFloat(), null)
    canvas.save()
    canvas.restore()
    return newBitmap
}


/**
 * 缩放Bitmap
 *
 * @param bm
 * @param f
 * @return
 */
fun zoomImg(bm: Bitmap, f: Float): Bitmap {
    val width = bm.width
    val height = bm.height
    val matrix = Matrix()
    matrix.postScale(f, f)
    return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true)
}

/**
 * get SymbolSet Result data
 * @receiver ImageScanner?
 * @return String
 */
fun ImageScanner.decoding(): String {
    var builder = StringBuilder()
    return results.forEach {
        builder.append(it.data)
    }.let {
        builder.toString()
    }
}
