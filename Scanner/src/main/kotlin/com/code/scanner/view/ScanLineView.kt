package com.code.scanner.view

import android.animation.ValueAnimator
import android.animation.ValueAnimator.*
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.code.scanner.R
import com.code.scanner.view.ScanLineView.Orientation.HORIZONTAL
import com.code.scanner.view.ScanLineView.Orientation.VERTICAL


/**
 * <pre>
 * author : leo
 * time   : 2019/12/03
 * desc   : ScanningLineView
 *          Used to handle up and down scan animation
 * <pre>
 *
 * @property scanLinePaint  画笔
 * @property gradientShader  线性渐变着色器
 * @property scanSpeed 扫描线，上下扫动速度
 * @property lineColor 扫描线颜色
 */
open class ScanLineView : View, LifecycleObserver {
    var scanSpeed: Long = 3000L
        set(value) {
            scanAnimator.duration = scanSpeed
            field = value
        }

    @ColorInt
    var lineColor = Color.RED
        set(value) {
            field = value
            gradientShader = null
            invalidate()
        }
    private val scanLinePaint: Paint by lazy {
        Paint()
    }
    private var gradientShader: Shader? = null
    private val scanAnimator: ValueAnimator by lazy {
        createValueAnimator()
    }
    private var lifecycle: Lifecycle? = null
    private var scanWidth: Int = 0
    private var scanHeight: Int = 0
    private var lineWidth: Int = 0
    private var orientation: Orientation = VERTICAL
    private var drawStopX: Int = 0
    private var drawStopY: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        obtainStyled(context, attrs)
        initPaint()
        initDrawPosition()
    }


    /**
     * 初始化画笔属性
     */
    private fun initPaint() {
        scanLinePaint.style = Paint.Style.FILL
        scanLinePaint.strokeWidth = 10f
        scanLinePaint.isAntiAlias = true
        gradientShader = createPaintShader()
        scanLinePaint.strokeWidth = lineWidth.toFloat()
        scanLinePaint.shader = gradientShader
    }


    /**
     * 初始化绘制位置
     */
    private fun initDrawPosition() {
        if (orientation == VERTICAL) {
            drawStopX = scanWidth
            drawStopY = 0
        } else {
            drawStopX = 0
            drawStopY = scanHeight
        }
    }


    /**
     * 测量控件大小
     * @param widthMeasureSpec Int
     * @param heightMeasureSpec Int
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(scanWidth, scanHeight)
    }


    /**
     * 参数值扫描方向
     * @return ValueAnimator
     */
    private fun createValueAnimator(): ValueAnimator = let {
        ofFloat(0f, 1f, 0f)
            .apply {
                interpolator = LinearInterpolator()
                duration = scanSpeed
                repeatMode = RESTART
                repeatCount = INFINITE
                addUpdateListener {
                    val value = it.animatedValue as Float
                    if (orientation == VERTICAL) {
                        translationY = value * height
                    } else {
                        translationX = value * width
                    }
                }
            }
    }


    /**
     * 读取xml自定义属性
     * @param context Context
     * @param attrs AttributeSet?
     */
    private fun obtainStyled(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.scan_line_view)
        lineColor = a.getColor(R.styleable.scan_line_view_scan_line_color, Color.GREEN)
        lineWidth = a.getDimensionPixelOffset(R.styleable.scan_line_view_scan_line_width, 10)
        scanWidth = a.getDimensionPixelOffset(R.styleable.scan_line_view_scan_width, 0)
        scanHeight = a.getDimensionPixelOffset(R.styleable.scan_line_view_scan_height, 0)
        orientation = a.getInt(R.styleable.scan_line_view_scan_orientation, 0)
            .let { Orientation.parse(it) }
        checkNotNull(scanWidth != 0 && scanHeight != 0) {
            "Please set the scan width and height！！！"
        }
        a.recycle()
    }


    /**
     * 绘制扫描线
     * @param canvas Canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(0f, 0f, drawStopX.toFloat(), drawStopY.toFloat(), scanLinePaint)
    }

    /**
     * 设置画笔着色器
     * @return LinearGradient
     */
    private fun createPaintShader(): LinearGradient = let {
        if (orientation == VERTICAL) {
            LinearGradient(
                0f,
                0f,
                scanWidth.toFloat(),
                0f,
                interceptionColor(lineColor),
                null,
                Shader.TileMode.CLAMP
            )
        } else {
            LinearGradient(
                0f,
                0f,
                0f,
                scanHeight.toFloat(),
                interceptionColor(lineColor),
                null,
                Shader.TileMode.CLAMP
            )
        }

    }


    /**
     * 截取渐变色
     * @return IntArray
     */
    private fun interceptionColor(@ColorInt color: Int): IntArray {
        return Integer.toHexString(color)
            .run {
                substring(length - 6, length - 0)
            }
            .let {
                intArrayOf(
                    Color.parseColor("#00$it"),
                    color, Color.parseColor("#00$it")
                )
            }
    }


    /**
     * Sets the lifecycle owner for this view. This means you don't need
     * to call [.open], [.close] or [.destroy] at all.
     *
     * @param owner the owner activity or fragment
     */
    private fun setLifecycleOwner(owner: LifecycleOwner) {
        if (lifecycle != null) {
            lifecycle?.removeObserver(this)
        }
        lifecycle = owner.lifecycle
        lifecycle?.addObserver(this)
    }


    /**
     * 设置扫描尺寸
     * @param width Int
     * @param height Int
     * @return ScanLineView
     */
    open fun setScanSize(width: Int, height: Int) = apply {
        this.scanWidth = width
        this.scanHeight = height
        layoutParams.width = width
        layoutParams.height = height
        initPaint()
        initDrawPosition()
    }

    /**
     * 设置扫描方向
     * @param orientation Orientation
     * @return ScanLineView
     */
    open fun setOrientation(orientation: Orientation) = apply {
        this.orientation = orientation
        initPaint()
        initDrawPosition()
    }


    /**
     * 扫描动画与LifecycleOwner绑定，支持自动播放/暂停动画
     * @param owner LifecycleOwner
     */
    open fun start(owner: LifecycleOwner) {
        setLifecycleOwner(owner)
    }


    /**
     * 暂停扫描动画
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onStop() {
        scanAnimator.cancel()
    }

    /**
     * 启动扫描动画
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        scanAnimator.start()
    }


    /**
     * 扫描方向
     * @property value Int
     * @constructor
     * @property VERTICAL 垂直方向
     * @property HORIZONTAL 水平方向
     */
    enum class Orientation(private val value: Int) {
        VERTICAL(0),
        HORIZONTAL(1);

        companion object {

            /**
             * value to Orientation
             * @param value Int
             * @return Orientation
             */
            @JvmStatic
            fun parse(value: Int): Orientation {
                return values().singleOrNull {
                    it.value == value
                } ?: VERTICAL
            }
        }
    }

}
