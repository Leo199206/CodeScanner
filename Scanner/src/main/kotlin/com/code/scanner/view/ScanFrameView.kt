package com.code.scanner.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.graphics.ColorUtils
import com.code.scanner.R

/**
 * <pre>
 *     author : kirito
 *     time   : 2020/05/27
 *     desc   : 扫码框（包含背景）
 * </pre>
 */
/**
 * <pre>
 *     author : kirito
 *     time   : 2020/05/27
 *     desc   : 扫码框（包含背景）
 * </pre>
 *
 * @property frameCornerColor Int 扫描框边角颜色
 * @property frameCornerLineBreadth  扫描框边角线宽
 * @property frameCornerLineExtent 扫描框边角线长
 * //@property frameCornerRadius 扫描框圆角半径
 * @property frameWidth 扫描框宽度
 * @property frameHeight  扫描框高度
 * @property scanningViewBgColor 扫描框控件背景
 * @property frameTopDistance 扫描框顶部距离
 * @property bgPaint Paint
 * @property cornerPaint Paint
 */
@Suppress("DEPRECATION")
open class ScanFrameView : View {
    @ColorInt
    private var frameCornerColor = Color.GREEN

    @Dimension
    private var frameCornerLineBreadth = 0

    @Dimension
    private var frameCornerLineExtent = 0

    //@Dimension
    //private var frameCornerRadius: Float = 0f

    @Dimension
    private var frameWidth = 0

    @Dimension
    private var frameHeight = 0

    @ColorInt
    private var scanningViewBgColor = 0

    @Dimension
    private var frameTopDistance = 0
    private lateinit var bgPaint: Paint
    private lateinit var cornerPaint: Paint

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        obtainStyled(context, attrs)
        initBackgroundPaint()
        initCornerPaint()
    }

    /**
     * 初始化背景画笔
     */
    private fun initBackgroundPaint() {
        bgPaint = Paint()
        bgPaint.isAntiAlias = true
        bgPaint.isDither = true
        bgPaint.style = Paint.Style.FILL
        bgPaint.color = ColorUtils.setAlphaComponent(Color.BLACK, 125)
    }

    /**
     * 初始化圆角画笔
     */
    private fun initCornerPaint() {
        cornerPaint = Paint()
        cornerPaint.style = Paint.Style.STROKE
        cornerPaint.isAntiAlias = true
        cornerPaint.color = frameCornerColor
        cornerPaint.strokeWidth = frameCornerLineBreadth.toFloat()
    }

    /**
     * 读取控件自定义属性
     *
     * @param context
     * @param attrs
     */
    private fun obtainStyled(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.scan_frame_view)
        //todo: Support the rounded，20200604日
        //frameCornerRadius = a.getDimension(R.styleable.scanning_frame_view_scanning_frame_corner_radius, 50f)
        frameCornerColor = a.getColor(R.styleable.scan_frame_view_scan_frame_corner_color, Color.GREEN)
        frameCornerLineBreadth = a.getDimensionPixelOffset(R.styleable.scan_frame_view_scan_frame_corner_line_breadth, 5)
        frameCornerLineExtent = a.getDimensionPixelOffset(R.styleable.scan_frame_view_scan_frame_corner_line_extent, 50)
        scanningViewBgColor = a.getColor(R.styleable.scan_frame_view_scan_frame_bg_color, ColorUtils.setAlphaComponent(Color.BLACK, 80))
        frameWidth = a.getDimensionPixelOffset(R.styleable.scan_frame_view_scan_frame_width, 200)
        frameHeight = a.getDimensionPixelOffset(R.styleable.scan_frame_view_scan_frame_height, 200)
        frameTopDistance = a.getDimensionPixelOffset(R.styleable.scan_frame_view_scan_frame_top_distance, 150)
        a.recycle()
    }


    /**
     * 绘制扫描框
     * @param canvas Canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (visibility != GONE) {
            drawFrame(canvas)
            drawCorner(canvas)
        }
    }


    /**
     * 绘制扫码背景
     * @param canvas Canvas
     */
    private fun drawFrame(canvas: Canvas) {
        val frameRectF = getFrameRectF()
        with(Path()) {
            //addRoundRect(frameRectF, frameCornerRadius, frameCornerRadius, Path.Direction.CCW)
            addRect(frameRectF, Path.Direction.CCW)
            canvas.run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    clipOutPath(this@with)
                } else {
                    clipPath(this@with, Region.Op.DIFFERENCE)
                }
                drawColor(scanningViewBgColor)
            }
        }
    }


    /**
     * Computes the draw position of the rectangle
     * @return RectF
     */
    private fun getFrameRectF(): RectF {
        val startLeft = ((measuredWidth - frameWidth) / 2).toFloat()
        val startTop = frameTopDistance.toFloat()
        val offset = (frameCornerLineBreadth / 2).toFloat()
        val left = startLeft + offset
        val top = startTop + offset
        val right = startLeft + frameWidth - offset
        val bottom = startTop + frameHeight - offset
        return RectF(left, top, right, bottom)
    }


    /**
     * 绘制扫描高亮边框
     * @param canvas Canvas
     */
    private fun drawCorner(canvas: Canvas) {
        val startX = ((measuredWidth - frameWidth) / 2).toFloat()
        val startY = frameTopDistance.toFloat()
        // left top
        canvas.drawLine(startX, startY + frameCornerLineBreadth / 2, startX + frameCornerLineExtent.toFloat(), startY + frameCornerLineBreadth / 2, cornerPaint)
        canvas.drawLine(startX + frameCornerLineBreadth / 2, startY + frameCornerLineExtent.toFloat(), startX + frameCornerLineBreadth / 2, startY, cornerPaint)
        // left bottom
        canvas.drawLine(
                startX, startY - frameCornerLineBreadth / 2 + frameHeight.toFloat(),
                startX + frameCornerLineExtent.toFloat(), startY - frameCornerLineBreadth / 2 + frameHeight.toFloat(), cornerPaint)
        canvas.drawLine(
                startX + frameCornerLineBreadth / 2, startY + frameHeight.toFloat(),
                startX + frameCornerLineBreadth / 2, startY + frameHeight.toFloat() - frameCornerLineExtent.toFloat(), cornerPaint)

        // right top
        canvas.drawLine(
                startX + frameWidth.toFloat(), startY + frameCornerLineBreadth / 2,
                startX + frameWidth.toFloat() - frameCornerLineExtent.toFloat(), startY + frameCornerLineBreadth / 2, cornerPaint)
        canvas.drawLine(startX + frameWidth.toFloat() - frameCornerLineBreadth / 2, startY + frameCornerLineExtent.toFloat(),
                startX + frameWidth.toFloat() - frameCornerLineBreadth / 2, startY, cornerPaint)

        // right bottom
        canvas.drawLine(startX + frameWidth.toFloat(), startY - frameCornerLineBreadth / 2 + frameHeight.toFloat(),
                startX - frameCornerLineExtent.toFloat() + frameWidth.toFloat(), startY - frameCornerLineBreadth / 2 + frameHeight.toFloat(), cornerPaint)
        canvas.drawLine(startX + frameWidth.toFloat() - frameCornerLineBreadth / 2, startY + frameHeight.toFloat(),
                startX + frameWidth.toFloat() - frameCornerLineBreadth / 2, startY + frameHeight.toFloat() - frameCornerLineExtent.toFloat(), cornerPaint)
    }

    /**
     * 设置扫描框大小
     *
     * @param width
     * @param height
     */
    fun setScanningFrameSize(width: Int, height: Int) {
        frameWidth = width
        frameHeight = height
        invalidate()
    }

}
