package com.scanner.sample

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.BarUtils
import com.code.scanner.bean.CodecType
import com.scanner.sample.databinding.ActivityMainBinding


/**
 * 首页入口
 */
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.setStatusBarColor(this, getColor(R.color.purple_500))
        binding.data = this
    }


    /**
     * 二维码扫描
     */
    fun actionScanQrCode() {
        ScannerActivity.start(this, CodecType.QR)
    }

    /**
     * 条形码扫描
     */
    fun actionScanBrCode() {
        ScannerActivity.start(this, CodecType.BAR)
    }


}