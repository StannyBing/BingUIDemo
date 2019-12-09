package com.zx.bui.ui.buicheckbox

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.zx.bui.BUIConfig
import com.zx.bui.R
import com.zx.bui.util.BUITool
import com.zx.bui.util.getBaseline
import kotlin.math.PI
import kotlin.math.cos


/**
 * Created by Xiangb on 2019/12/4.
 * 功能：
 */
class BUICheckBox @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : View(context, attributeSet, defStyle) {

    private val ringPaint = Paint()//未选中圆环画笔
    private val circlePaint = Paint()//选中圆环画笔
    private val checkPaint = Paint()//选择画笔
    private val textPaint = Paint()//文字画笔

    private var path = Path()//路径
    private var point = PointF()
    private var rect = Rect()
    private var tempProgress = 0.0f
    private var lineEms = 0
    private var tempText = ""
    private var maxLines = 1

    private var checkedChange: (Boolean) -> Unit = {}//切换监听

    @ColorInt
    var checkedColor: Int
    @ColorInt
    var uncheckedColor: Int

    var isCheckClickable: Boolean//是否可点击
    var showCheckAnim: Boolean//是否展示动画
    var checkPadding: Float//绘图间隔
    var checkRadius: Float//图标半径
    private var animProgress = 1.0f
        set(value) {
            field = value
            invalidate()
        }

    private var isChecked = false//是否选中
    private var mCheckText = ""//绘制文本

    init {
        val typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.BUICheckBox)
        isChecked = typeArray.getBoolean(R.styleable.BUICheckBox_isChecked, false)
        checkedColor = typeArray.getColor(R.styleable.BUICheckBox_color_checked, BUIConfig.uiColor)
        uncheckedColor = typeArray.getColor(R.styleable.BUICheckBox_color_unchecked, ContextCompat.getColor(context, R.color.bui_gray))
        mCheckText = if (typeArray.hasValue(R.styleable.BUICheckBox_check_text)) typeArray.getString(R.styleable.BUICheckBox_check_text) else ""
        checkRadius = typeArray.getDimension(R.styleable.BUICheckBox_check_radius, BUITool.dp2px(context, 10f).toFloat())
        showCheckAnim = typeArray.getBoolean(R.styleable.BUICheckBox_check_anim, true)
        isCheckClickable = typeArray.getBoolean(R.styleable.BUICheckBox_check_clickable, true)
        checkPadding = typeArray.getDimension(R.styleable.BUICheckBox_check_padding, BUITool.dp2px(context, 5f).toFloat())

        setWillNotDraw(false)
        ringPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
            color = uncheckedColor
            isAntiAlias = true
        }
        circlePaint.apply {
            style = Paint.Style.FILL
            color = checkedColor
            isAntiAlias = true
        }
        checkPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = 4f
            color = Color.WHITE
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }
        textPaint.apply {
            style = Paint.Style.FILL
            strokeWidth = 2f
            color = checkedColor
            isAntiAlias = true
            textSize = typeArray.getDimension(R.styleable.BUICheckBox_text_size, BUITool.dp2px(context, BUIConfig.uiSize).toFloat())
        }

        setOnClickListener {
            if (isCheckClickable) {
                setChecked(!isChecked)
            }
        }
        typeArray.recycle()
    }

    /**
     * 返回当前选中状态
     */
    fun isChecked(): Boolean {
        return isChecked
    }

    /**
     * 设置选中状态
     */
    fun setChecked(isChecked: Boolean, anim: Boolean = true) {
        this.isChecked = isChecked
        checkedChange(isChecked)
        if (anim && showCheckAnim) {
            startAnim()
        } else {
            invalidate()
        }
    }

    /**
     * 切换监听
     */
    fun setCheckedChangeListener(checkedChange: (Boolean) -> Unit) {
        this.checkedChange = checkedChange
    }

    /**
     * 开启动画
     */
    private fun startAnim() {
        val animator = ObjectAnimator.ofFloat(this, "animProgress", 0.0f, 1.0f)
        animator.duration = 300
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    override fun onDraw(canvas: Canvas?) {
        //绘制图标
        canvas?.save()
        path.reset()
        path.fillType = Path.FillType.INVERSE_WINDING
        path.addCircle(checkPadding + checkRadius, height / 2f, checkRadius * (if (isChecked) 1 - animProgress else animProgress), Path.Direction.CW)
        if (animProgress < 1.0f) {
            canvas?.clipPath(path)
        }
        if (isChecked || animProgress < 1.0f) {
            canvas?.drawCircle(checkPadding + checkRadius, height / 2f, checkRadius - (if (animProgress > 0.5f) 1 - animProgress else animProgress) * checkRadius * 0.4f, circlePaint)
        }
        canvas?.restore()
        if (!isChecked && animProgress == 1.0f) {
            canvas?.drawCircle(checkPadding + checkRadius, height / 2f, checkRadius, ringPaint)
        }
        //绘制勾选路径
        if (isChecked || animProgress != 1.0f) {
            if (animProgress > 0.7f) {
                tempProgress = if (!isChecked) (1 - animProgress) / 0.3f else (animProgress - 0.7f) / 0.3f
                path.reset()
                path.fillType = Path.FillType.WINDING
                point.set(checkPadding + checkRadius * 4 / 5, height / 2f + checkRadius / 3f)
                path.moveTo((point.x - cos(PI / 180.0 * 45) * checkRadius / 2f * tempProgress).toFloat()
                        , (point.y - cos(PI / 180.0 * 45) * checkRadius / 2f * tempProgress).toFloat())
                path.lineTo(point.x, point.y)
                path.lineTo((point.x + cos(PI / 180.0 * 45) * checkRadius / 1f * tempProgress).toFloat()
                        , (point.y - cos(PI / 180.0 * 45) * checkRadius / 1f * tempProgress).toFloat())
                canvas?.drawPath(path, checkPaint)
            }
        }
        //绘制文字
        if (mCheckText.isNotEmpty()) {
            textPaint.color = if (isChecked) {
                checkedColor
            } else {
                uncheckedColor
            }
            if (maxLines > 1) {//当文字无法在一排内显示
                for (index in 0 until maxLines) {//逐行绘制文字
                    textPaint.getTextBounds(mCheckText, index * lineEms, if ((index + 1) * lineEms < mCheckText.length) (index + 1) * lineEms else mCheckText.length, rect)
                    tempText = mCheckText.substring(index * lineEms, if ((index + 1) * lineEms < mCheckText.length) (index + 1) * lineEms else mCheckText.length)
                    canvas?.drawText(tempText, checkPadding * 2 + checkRadius * 2, if (maxLines % 2 == 0) {//偶数行
                        height / 2 + textPaint.getBaseline() + (index - maxLines / 2) * rect.height() + rect.height() / 2
                    } else {//奇数行
                        height / 2 + textPaint.getBaseline() + (index - maxLines / 2) * rect.height()
                    }, textPaint)
                }
            } else {
                canvas?.drawText(mCheckText, checkPadding * 2 + checkRadius * 2, height / 2 + textPaint.getBaseline(), textPaint)
            }
        }
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        textPaint.getTextBounds(mCheckText, 0, mCheckText.length, rect)
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = (checkPadding * 2 + checkRadius * 2).toInt()
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            if (mCheckText.isEmpty()) {
                widthSize = (checkPadding * 2 + checkRadius * 2).toInt()
            } else {
                widthSize = (checkPadding * 3 + checkRadius * 2 + rect.width()).toInt()
            }
        } else if (widthMode == MeasureSpec.EXACTLY && rect.width() > widthSize - checkPadding * 2 - checkRadius * 2) {//当文字无法在一排内显示
            for (index in 1..mCheckText.length) {//计算出一行最多的文字数量
                textPaint.getTextBounds(mCheckText, 0, index, rect)
                if (rect.width() < widthSize - checkPadding * 3 - checkRadius * 2) {
                    lineEms = index
                } else {
                    break
                }
            }
            maxLines = mCheckText.length / lineEms + if (mCheckText.length % lineEms == 0) 0 else 1
            if (heightMode == MeasureSpec.AT_MOST) {
                heightSize = Math.max((checkPadding * 2 + checkRadius * 2).toInt(), (checkPadding * 2 + maxLines * rect.height()).toInt())
            }
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, widthMode), MeasureSpec.makeMeasureSpec(heightSize, heightMode))
    }

}