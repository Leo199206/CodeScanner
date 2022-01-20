#### 使用说明文档
基于[CameraView](https://github.com/natario1/CameraView)封装，秒启动二维码/条形码扫描组件

#### 效果预览

<img src="https://github.com/Leo199206/CodeScanner/blob/main/1642646855874575.gif?raw=true" width="300" heght="500" align=center />

#### 依赖

+ 添加maven仓库配置到项目根目录gradle文件下

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

+ 添加以下maven依赖配置到app模块，gradle文件下

```
implementation  'com.github.Leo199206:Scanner:1.0.0'
implementation  'com.github.Leo199206:Zbar:1.0.0'
```

#### 添加到布局

```
    <com.code.scanner.view.ScanCameraView
            android:id="@+id/scan_camera"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

        <com.code.scanner.view.ScanFrameView
            android:id="@+id/scan_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_gravity="center_horizontal"
            app:scan_frame_bg_color="@color/black_translucent"
            app:scan_frame_corner_color="@color/teal_200"
            app:scan_frame_corner_line_breadth="3dp"
            app:scan_frame_corner_line_extent="15dp"
            app:scan_frame_height="266dp"
            app:scan_frame_top_distance="200dp"
            app:scan_frame_width="266dp" />

        <com.code.scanner.view.ScanLineView
            android:id="@+id/line_view"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:layout_marginTop="200dp"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:scan_height="266dp"
            app:scan_line_color="@color/teal_200"
            app:scan_width="266dp" />

```

+ 代码配置
> 具体使用详情请参考ScannerActivity，以下仅仅为代码片段

```
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

```


#### 支持

+ 使用过程中，如有问题或者建议，欢迎提出issue
+ 如遇issue解决不了的问题，可以邮件联系：850884963@qq.com
+ 如果该库对你有帮助，请动动你的小手指，给个star

#### LICENSE

SlideUnlock is under the Apache License Version 2.0. See
the [LICENSE](https://raw.githubusercontent.com/Leo199206/CodeScaner/main/LICENSE) file for
details.
