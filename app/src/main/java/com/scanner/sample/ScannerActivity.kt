package com.scanner.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.code.scanner.bean.CodecType
import com.code.scanner.view.ScanLineView
import com.scanner.sample.databinding.ActivityScanBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.internal.functions.Functions

/**
 * Created on 2021/08/09
 *
 * @Author liuzhou(leo)
 * @Detail 扫码Demo
 */
class ScannerActivity : AppCompatActivity() {
    private val binding: ActivityScanBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_scan)
    }
    private var codeType: CodecType = CodecType.QR
    var modeTitle: ObservableField<String> = ObservableField()
    val isTorchOn = ObservableBoolean(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.transparentStatusBar(this)
        binding.data = this
        setModeTitle()
        setScanCodeStyle()
        startScanCamera()
    }


    /**
     * 扫码模式标题
     */
    private fun setModeTitle() {
        codeType = intent.getIntExtra(DATA, CodecType.QR.value).let {
            CodecType.parse(it)
        }
        if (codeType == CodecType.BAR) {
            "条形码扫描"
        } else {
            "二维码扫码"
        }.let {
            modeTitle.set(it)
        }
    }


    /**
     * 设置扫码框样式
     */
    private fun setScanCodeStyle() {
        if (codeType == CodecType.BAR) {
            val width = SizeUtils.dp2px(260f)
            val height = SizeUtils.dp2px(133f)
            setScanCodeStyle(width, height, 3000, ScanLineView.Orientation.HORIZONTAL)
        } else {
            val size = SizeUtils.dp2px(260f)
            setScanCodeStyle(size, size, 3000, ScanLineView.Orientation.VERTICAL)
        }
    }


    /**
     * 扫码框样式
     * @param width Int
     * @param height Int
     * @param scanSpeed Long
     */
    private fun setScanCodeStyle(
        width: Int,
        height: Int,
        scanSpeed: Long,
        orientation: ScanLineView.Orientation
    ) {
        binding.scanView.setScanningFrameSize(width, height)
        binding.lineView.apply {
            this.setScanSize(width, height)
            this.setOrientation(orientation)
            this.scanSpeed = scanSpeed
            this.start(this@ScannerActivity)
        }
    }


    /**
     * 启动扫码
     */
    private fun startScanCamera() {
        binding.scanCamera
            .apply {
                playSounds = false
            }
            .startScan(this)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                ToastUtils.showLong("扫码结果：${it}")
            }
            .subscribe(Functions.emptyConsumer(), Functions.ERROR_CONSUMER)
    }


    /**
     * 从相册选择二维码
     */
    fun actionGallery() {
    }


    /**
     * 切换手电筒开关
     */
    fun actionSwitchTorch() {
        binding.scanCamera.switchTorch().let {
            isTorchOn.set(it)
        }
    }


    /**
     * 关闭页面
     */
    fun actionFinish() {
        finish()
    }


    companion object {
        const val DATA = "data"

        /**
         * Open ScanActivity
         * @param codecType CodecType
         */
        @JvmStatic
        fun start(context: Context, codecType: CodecType) {
            val intent = Intent(context, ScannerActivity::class.java)
            intent.putExtra(DATA, codecType.value)
            context.startActivity(intent)
        }
    }

}