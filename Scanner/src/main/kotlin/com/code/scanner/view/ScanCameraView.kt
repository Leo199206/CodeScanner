package com.code.scanner.view

import android.content.Context
import android.graphics.ImageFormat
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.lifecycle.LifecycleOwner
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import com.code.scanner.R
import com.code.scanner.bean.CodecType
import com.code.scanner.core.ImageDecoder
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * <pre>
 *     author : leo
 *     time   : 2020/06/12
 *     desc   : 基于[com.otaliastudios.cameraview.CameraView]封装，扫码预览相机
 * </pre>
 */
class ScanCameraView : CameraView, FrameProcessor, ImageDecoder.ImageDecoderResultListener {
    private var publishSubject: PublishSubject<String> = PublishSubject.create()
    private val decoder: ImageDecoder = ImageDecoder(this)
    private var decoderResultCallback: ((String) -> Unit)? = null
    private val handlers: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val autoFocusRunnable: Runnable by lazy {
        Runnable {
            startAutoFocus()
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initCamera()
        obtainStyled(context, attrs)
    }


    /**
     * 读取xml自定义属性
     * @param context Context
     * @param attrs AttributeSet?
     */
    private fun obtainStyled(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.scan_camera_view)
        val typeValue = a.getInt(R.styleable.scan_camera_view_scan_code_type, CodecType.ALL.value)
        setCodecType(CodecType.parse(typeValue))
        a.recycle()
    }


    /**
     * 设置解码类型
     * @param type CodecType
     */
    fun setCodecType(type: CodecType) {
        decoder.applyCodecType(type)
    }


    /**
     * 相机初始化
     */
    private fun initCamera() {
        playSounds = false
        addFrameProcessor(this)
    }


    /**
     * 自动对焦扫码
     */
    private fun startAutoFocus() {
        val x = width / 2.0f
        var y = height / 2.0f
        startAutoFocus(x, y)
        handlers.postDelayed(autoFocusRunnable, 2000)
    }


    /**
     * 相机预览帧回调处理
     * @param frame Frame
     */
    override fun process(frame: Frame) {
        if (frame.format == ImageFormat.NV21 && frame.dataClass == ByteArray::class.java) {
            val data: ByteArray = frame.getData()
            decoder.decodeByteArray(data, frame.size.width, frame.size.height)
        }
    }


    /**
     * 切换前后摄像头
     */
    fun switchFacing() {
        facing = if (facing == Facing.BACK) {
            Facing.FRONT
        } else {
            Facing.BACK
        }
    }


    /**
     * 是否是后置摄像头
     * @return Boolean
     */
    fun isBackFacing(): Boolean {
        return facing == Facing.BACK
    }

    /**
     * 是否是前置摄像头
     * @return Boolean
     */
    fun isFrontFacing(): Boolean {
        return facing == Facing.FRONT
    }


    /**
     * 开/关闪光灯
     */
    fun switchTorch(): Boolean = let {
        if (isOnTorch()) {
            offTorch()
        } else {
            onTorch()
        }
        isOnTorch()
    }

    /**
     * 手电筒 - 打开
     */
    fun onTorch() {
        flash = Flash.TORCH
    }

    /**
     * 手电筒 - 关闭
     */
    fun offTorch() {
        flash = Flash.OFF
    }


    /**
     * 判断手电筒是否开启
     * @return Boolean
     */
    fun isOnTorch(): Boolean {
        return flash == Flash.TORCH
    }


    /**
     * 启动扫码，并订阅结果回调（RxJava方式）
     * @return Observable<String>
     */
    fun startScan(owner: LifecycleOwner): Observable<String> = let {
        setLifecycleOwner(owner)
        startAutoFocus()
        publishSubject.toFlowable(BackpressureStrategy.BUFFER).toObservable()
    }


    /**
     * 设置扫码结果回调接口（普通接口回调方式）
     * @param resultCallback
     */
    fun startScan(owner: LifecycleOwner, resultCallback: (String) -> Unit) {
        this.decoderResultCallback == resultCallback
        setLifecycleOwner(owner)
        startAutoFocus()
    }


    /**
     * Stops the current preview, if any was started.
     * This should be called onPause().
     */
    override fun close() {
        super.close()
        handlers.removeCallbacks(autoFocusRunnable)
    }

    /**
     * Destroys this instance, releasing immediately
     * the camera resource.
     */
    override fun destroy() {
        super.destroy()
        handlers.removeCallbacks(autoFocusRunnable)
    }

    /**
     * 图片解码回调
     * @param result String
     */
    override fun onImageDecodingResult(result: String) {
        publishSubject.onNext(result)
        decoderResultCallback?.invoke(result)
    }


}
