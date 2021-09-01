package com.code.scanner.core

import android.os.Handler
import android.util.Log
import com.code.scanner.bean.CodecType
import com.code.scanner.helper.decoding
import com.code.zbar.Config
import com.code.zbar.Image
import com.code.zbar.ImageScanner
import com.code.zbar.Symbol
import java.util.concurrent.Executors

/**
 * <pre>
 *     author : leo
 *     time   : 2020/06/12
 *     desc   : 相机帧率解析器
 * </pre>
 * @property [isParsing] 是否正在解析
 * @property [scanType] 当前设置扫码类型
 * @property [isParsing] 是否正在解码
 * @property imageBean 图片数据
 * @property executor 线程池
 * @property resultListener 解码结果回调接口
 */
class ImageDecoder(private var resultListener: ImageDecoderResultListener) : Runnable {
    private val executor = Executors.newSingleThreadExecutor()
    private var scanner: ImageScanner = ImageScanner()
    private var scanType: CodecType = CodecType.ALL
    private var isParsing: Boolean = false
    private var imageBean: Image? = null
    private var handler: Handler = Handler()

    init {
        applyCodecType(scanType)
    }

    /**
     * 设置解码类型
     * @param scanType ScanType
     */
    fun applyCodecType(scanType: CodecType) {
        when (scanType) {
            CodecType.QR -> {
                applyQrCodeConfig()
            }
            CodecType.BAR -> {
                applyBarCodeConfig()
            }
            CodecType.CUSTOM -> {
                applyCustomCodeConfig()
            }
            else -> {
                applyAllCodeScanner()
            }

        }
    }

    /**
     * 应用二维码解码配置
     */
    private fun applyQrCodeConfig() {
        scanner.setConfig(Symbol.NONE, Config.ENABLE, 0)
        scanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1)
    }


    /**
     * 应用条形码解码配置
     */
    private fun applyBarCodeConfig() {
        scanner.setConfig(Symbol.NONE, Config.ENABLE, 0)
        scanner.setConfig(Symbol.CODE128, Config.ENABLE, 1)
        scanner.setConfig(Symbol.CODE39, Config.ENABLE, 1)
        scanner.setConfig(Symbol.EAN13, Config.ENABLE, 1)
        scanner.setConfig(Symbol.EAN8, Config.ENABLE, 1)
        scanner.setConfig(Symbol.UPCA, Config.ENABLE, 1)
        scanner.setConfig(Symbol.UPCE, Config.ENABLE, 1)
    }

    /**
     * 应用自定义解码类型
     */
    private fun applyCustomCodeConfig() {
        scanner.setConfig(Symbol.NONE, Config.ENABLE, 0)
        scanner.setConfig(Symbol.NONE, Config.ENABLE, 1)
    }


    /**
     * 应用所有类型编码解码配置
     */
    private fun applyAllCodeScanner() {
        scanner.setConfig(Symbol.NONE, Config.X_DENSITY, 3)
        scanner.setConfig(Symbol.NONE, Config.Y_DENSITY, 3)
        scanner.setConfig(Symbol.NONE, Config.ENABLE, 0)
        scanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1)
        scanner.setConfig(Symbol.CODE128, Config.ENABLE, 1)
        scanner.setConfig(Symbol.CODE39, Config.ENABLE, 1)
        scanner.setConfig(Symbol.EAN13, Config.ENABLE, 1)
        scanner.setConfig(Symbol.EAN8, Config.ENABLE, 1)
        scanner.setConfig(Symbol.UPCA, Config.ENABLE, 1)
        scanner.setConfig(Symbol.UPCE, Config.ENABLE, 1)
    }


    /**
     * 执行解析[ByteArray]原始图片数据任务
     * @param imageData 图片原始数据
     * @param width 图片宽度
     * @param height 图片高度
     */
    fun decodeByteArray(imageData: ByteArray, width: Int, height: Int) {
        //正在解析，先返回，等待解析完成
        if (isParsing) {
            return
        }
        isParsing = true
        imageBean = Image(width, height, "Y800")
        imageBean?.data = imageData
        executor.execute(this)
    }


    /**
     * 图片解析为耗时操作，在子线程中处理
     */
    override fun run() {
        scanner.scanImage(imageBean)
        var resultStr: String = scanner.decoding()
        Log.d("${javaClass.simpleName}:", "decoding:${resultStr}")
        if (!resultStr.isNullOrEmpty()) {
            resultListener.onImageDecodingResult(resultStr)
            delayDecode()
        } else {
            isParsing = false
        }
    }


    /**
     * 延时恢复扫描
     */
    private fun delayDecode() {
        handler.postDelayed({ isParsing = false }, 2000)
    }


    /**
     * 图片解码结果回调接口
     */
    interface ImageDecoderResultListener {
        /**
         * 图片解码结果回调
         * @param result String
         */
        fun onImageDecodingResult(result: String)
    }
}
