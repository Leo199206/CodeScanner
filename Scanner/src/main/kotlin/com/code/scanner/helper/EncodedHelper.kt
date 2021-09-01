/**
 * <pre>
 *     author : leo
 *     time   : 2020/06/03
 *     desc   : 编码工具
 * </pre>
 */

@file:Suppress("DEPRECATION")

package com.code.scanner.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.code.scanner.builder.TextViewConfig
import java.io.UnsupportedEncodingException
import java.util.*


/**
 * <pre>
 *     author : leo
 *     time   : 2020/06/12
 *     desc   : 编码工具类
 * </pre>
 */
object EncodedHelper {
    /**
     * 创建二维码
     * @param content String
     * @param width Int
     * @param height Int
     * @return Bitmap
     */
    @JvmStatic
    fun createQrCode(content: String, width: Int = 300, height: Int = 300): Bitmap {
        val bitmap: Bitmap
        var result: BitMatrix? = null
        val multiFormatWriter = MultiFormatWriter()
        val hints = Hashtable<EncodeHintType, Any?>()
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.Q //这里调整二维码的容错率
        hints[EncodeHintType.MARGIN] = 1 //设置白边取值1-4，值越大白边越大
        try {
            result = multiFormatWriter.encode(String(content.toByteArray(charset("UTF-8")), charset("ISO-8859-1")),
                    BarcodeFormat.QR_CODE, width, height, hints)
        } catch (e: WriterException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        val w = result!!.width
        val h = result.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (result[x, y]) Color.BLACK else Color.WHITE
            }
        }
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        return bitmap
    }

    /**
     * 生成带logo的二维码
     *
     * @param content
     * @param logo
     * @return
     */
    @JvmStatic
    fun createQrCodeBeltLogo(content: String, logo: Bitmap): Bitmap? {
        return createQrCodeBeltLogo(content, 300, 300, logo, 0.3f)
    }

    /**
     * 生成带logo的二维码
     *
     * @param content
     * @param width
     * @param height
     * @param logo
     * @param scaleValue（logo大小默认为二维码大小 * 0.3f
     * Logo太大可能导致二维码无法识别，也可以通过指定scaleValue来指定大小）
     * @return
     */
    @JvmStatic
    fun createQrCodeBeltLogo(content: String, width: Int, height: Int, logo: Bitmap, scaleValue: Float = 0.3f): Bitmap? {
        val qrCode = createQrCode(content, width, height)
        return createQrCodeBeltLogo(qrCode, logo, scaleValue)
    }

    /**
     * 生成带logo的二维码
     *
     * @param qrCode
     * @param scaleValue
     * @param logo
     * @return
     */
    @JvmStatic
    fun createQrCodeBeltLogo(qrCode: Bitmap, logo: Bitmap, scaleValue: Float): Bitmap? {
        val qrWidth = qrCode.width
        val waterWidth = (qrWidth * scaleValue).toInt()
        val scale = waterWidth / logo.width.toFloat()
        return createWaterMaskCenter(qrCode, zoomImg(logo, scale))
    }

    /**
     * 生成条形码
     *
     * @param context
     * @param contents
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    @JvmStatic
    fun createBarCode(context: Context, contents: String?, desiredWidth: Int = 300,
                      desiredHeight: Int = 150, config: TextViewConfig? = null): Bitmap? {
        if (TextUtils.isEmpty(contents)) {
            throw NullPointerException("contents not be null")
        }
        if (desiredWidth == 0 || desiredHeight == 0) {
            throw NullPointerException("desiredWidth or desiredHeight not be null")
        }
        val resultBitmap: Bitmap?

        // 条形码的编码类型
        val barcodeFormat = BarcodeFormat.CODE_128
        val barcodeBitmap = createBarCode(contents, barcodeFormat,
                desiredWidth, desiredHeight)
        val codeBitmap = createBarCode(contents, barcodeBitmap.width,
                barcodeBitmap.height, context, config)
        resultBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, PointF(
                0F, desiredHeight.toFloat()))
        return resultBitmap
    }

    /**
     * 生成条形码
     * @param contents
     * @param desiredHeight
     * @param desiredWidth
     * @return
     */
    @JvmStatic
    private fun createBarCode(contents: String?, format: BarcodeFormat, desiredWidth: Int, desiredHeight: Int): Bitmap {
        val white = -0x1
        val black = -0x1000000
        val writer = MultiFormatWriter()
        var result: BitMatrix? = null
        try {
            result = writer.encode(contents, format, desiredWidth,
                    desiredHeight, null)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        val width = result!!.width
        val height = result.height
        val pixels = IntArray(width * height)
        // All are 0, or black, by default
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result[x, y]) black else white
            }
        }
        val bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, desiredWidth, 0, 0, desiredWidth, height)
        return bitmap
    }

    /**
     * 生成条形码
     *
     * @param contents
     * @param width
     * @param height
     * @param textConfig
     * @return
     */
    @JvmStatic
    private fun createBarCode(contents: String?, width: Int, height: Int, context: Context,
                              textConfig: TextViewConfig?): Bitmap {
        var config = textConfig
        if (config == null) {
            config = TextViewConfig.Builder().build()
        }
        val tv = TextView(context)
        val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = config.gravity
        tv.width = width
        tv.height = height
        tv.text = contents
        tv.textSize = (if (config.size == 0) tv.textSize else config.size.toFloat())
        tv.setTextColor(config.color)
        tv.maxLines = config.maxLines
        tv.gravity = config.gravity
        tv.postInvalidate()
        tv.isDrawingCacheEnabled = true
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        val offset = width - tv.measuredWidth
        tv.layout(offset / 2, 0, width, tv.measuredHeight)
        tv.layoutParams = layoutParams
        tv.buildDrawingCache()
        return tv.drawingCache
    }
}
