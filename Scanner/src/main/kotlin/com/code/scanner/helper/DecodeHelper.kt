package com.code.scanner.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.code.scanner.helper.DecodeHelper.decodingConfig
import com.code.scanner.helper.DecodeHelper.formats
import com.code.zbar.Config
import com.code.zbar.Image
import com.code.zbar.ImageScanner
import com.code.zbar.Symbol
import com.google.zxing.*
import java.io.File
import java.util.*

/**
 * <pre>
 *     author : leo
 *     time   : 2020/06/03
 *     desc   : 解码工具
 * </pre>
 * @property decodingConfig 解码配置
 * @property formats 解码格式
 */
object DecodeHelper {
    private val decodingConfig: MutableMap<DecodeHintType, Any?> =
        EnumMap(DecodeHintType::class.java)
    private val formats = mutableListOf(
        BarcodeFormat.AZTEC, BarcodeFormat.CODABAR,
        BarcodeFormat.CODE_39, BarcodeFormat.CODE_93,
        BarcodeFormat.CODE_128, BarcodeFormat.DATA_MATRIX,
        BarcodeFormat.EAN_8, BarcodeFormat.EAN_13,
        BarcodeFormat.ITF, BarcodeFormat.MAXICODE,
        BarcodeFormat.PDF_417, BarcodeFormat.QR_CODE,
        BarcodeFormat.RSS_14, BarcodeFormat.RSS_EXPANDED,
        BarcodeFormat.UPC_A, BarcodeFormat.UPC_E,
        BarcodeFormat.UPC_EAN_EXTENSION
    )

    init {
        // 编码格式
        decodingConfig[DecodeHintType.POSSIBLE_FORMATS] = formats
        // 花更多的时间用于寻找图上的编码，优化准确性，但不优化速度
        decodingConfig[DecodeHintType.TRY_HARDER] = true
        // 复杂模式，开启 PURE_BARCODE 模式（带图片 LOGO 的解码方案）
        decodingConfig[DecodeHintType.TRY_HARDER] = true
        // 编码字符集
        decodingConfig[DecodeHintType.CHARACTER_SET] = "utf-8"
    }

    /**
     * 解析二维码
     *
     * @return
     */
    @JvmStatic
    fun decodingQrCode(imageFile: File): String? {
        check(imageFile.exists()) {
            "File does not exist!!!"
        }
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        return decodingQrCode(bitmap)
    }

    /**
     * 解析二维码
     * @param imageView ImageView
     * @return String?
     */
    @JvmStatic
    fun decodingQrCode(imageView: ImageView): String? {
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        return decodingQrCode(bitmap)
    }

    /**
     * 解析条形码
     *
     * @param imageFile
     * @return
     */
    @JvmStatic
    fun decodingBarCode(imageFile: File): String {
        if (!imageFile.exists()) {
            throw RuntimeException("File does not exist")
        }
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        return decodingBarCode(bitmap)
    }


    /**
     * 解析二维码
     *
     * @param bitmap
     * @return
     */
    @JvmStatic
    fun decodingQrCode(bitmap: Bitmap): String? {
        var result: Result
        var source: RGBLuminanceSource? = null
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            source = RGBLuminanceSource(width, height, pixels)
            result =
                MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(source)), decodingConfig)
            result.text
        } catch (e: Exception) {
            e.printStackTrace()
            if (source != null) {
                try {
                    result = MultiFormatReader().decode(
                        BinaryBitmap(GlobalHistogramBinarizer(source)),
                        decodingConfig
                    )
                    return result.text
                } catch (e2: Throwable) {
                    e2.printStackTrace()
                }
            }
            null
        }
    }

    /**
     * 读取Bitmap中条形码数据
     * @param barcodeBmp Bitmap
     * @return String
     */
    @JvmStatic
    fun decodingBarCode(barcodeBmp: Bitmap): String {
        val width = barcodeBmp.width
        val height = barcodeBmp.height
        val pixels = IntArray(width * height)
        barcodeBmp.getPixels(pixels, 0, width, 0, 0, width, height)
        val barcode = Image(width, height, "RGB4")
        barcode.setData(pixels)
        val reader = ImageScanner()
        reader.setConfig(Symbol.NONE, Config.ENABLE, 0)
        reader.setConfig(Symbol.CODE128, Config.ENABLE, 1)
        reader.setConfig(Symbol.CODE39, Config.ENABLE, 1)
        reader.setConfig(Symbol.EAN13, Config.ENABLE, 1)
        reader.setConfig(Symbol.EAN8, Config.ENABLE, 1)
        reader.setConfig(Symbol.UPCA, Config.ENABLE, 1)
        reader.setConfig(Symbol.UPCE, Config.ENABLE, 1)
        reader.setConfig(Symbol.UPCE, Config.ENABLE, 1)
        reader.scanImage(barcode.convert("Y800"))
        return reader.decoding()
    }

    /**
     * 读取条形码
     * @param imageView ImageView?
     * @return String
     */
    @JvmStatic
    fun decodingBarCode(imageView: ImageView): String {
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        return decodingBarCode(bitmap)
    }

}
